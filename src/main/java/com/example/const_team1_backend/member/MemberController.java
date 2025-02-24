package com.example.const_team1_backend.member;

import com.example.const_team1_backend.BaseController;
import com.example.const_team1_backend.common.exception.BadRequestException;
import com.example.const_team1_backend.common.exception.InvalidJwtTokenException;
import com.example.const_team1_backend.common.exception.TokenExpiredException;
import com.example.const_team1_backend.common.message.ErrorMessage;
import com.example.const_team1_backend.config.provider.JwtTokenProvider;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.facility.FacilityService;
import com.example.const_team1_backend.facility.dto.FacilityResponse;
import com.example.const_team1_backend.member.dto.*;
import com.example.const_team1_backend.review.Review;
import com.example.const_team1_backend.review.dto.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping(value = "/v1/members",produces = "application/json; charset=UTF-8")
public class MemberController extends BaseController<Member,MemberService> {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MemberTokenRepository memberTokenRepository;

    public MemberController(MemberService service, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        super(service);
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    @PostMapping("/join")
    public ResponseEntity<SuccessResponse> signup(@RequestBody MemberRequest request) {
        Optional<Member> found = service.findByLoginId(request.getLoginId());
        if (found.isPresent()) {
            throw new BadRequestException(ErrorMessage.ALREADY_USED_ID.getMessage());
        }

        Member member = new Member();
        member.setLoginId(request.getLoginId());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setUsername(request.getUsername());

        service.createMember(member);

        return ResponseEntity.ok(SuccessResponse.success("회원가입 성공"));
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest requestDto) {
        // 1. 기존 인증 로직
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getLoginId(),
                        requestDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. 멤버 조회
        Member member = service.findByLoginId(authentication.getName())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));

        // [추가] 24시간 내 생성된 리프레시 토큰 수 체크
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        long tokenCountLast24h = memberTokenRepository.countByMemberAndCreatedAtAfter(
                member,
                twentyFourHoursAgo
        );
        if (tokenCountLast24h >= 24) {
            throw new BadRequestException("24시간 내 24회 로그인 제한을 초과했습니다.");
        }

        // 3. 기존 토큰 3개 이상 시 오래된 토큰 삭제
        List<MemberToken> existingTokens = memberTokenRepository.findByMember(member);
        if (existingTokens.size() >= 3) {
            existingTokens.sort(Comparator.comparing(MemberToken::getLastLoginTime));
            memberTokenRepository.deleteAll(existingTokens.subList(0, existingTokens.size() - 2));
        }

        // 4. 새 토큰 생성
        Map<String, String> tokens = jwtTokenProvider.createToken(member.getLoginId()); // ✅ loginId 사용

        // 5. 토큰 정보 설정 및 저장
        MemberToken newToken = new MemberToken();
        newToken.setMember(member);
        newToken.setAccessToken(tokens.get("accessToken"));
        newToken.setRefreshToken(tokens.get("refreshToken"));
        newToken.setLastLoginTime(LocalDateTime.now());
        newToken.setRenewCount(0);
        newToken.setCreatedAt(LocalDateTime.now());
        memberTokenRepository.save(newToken);

        return ResponseEntity.ok(AuthResponse.fromTokenAndMemberId(
                member.getId(),
                member.getUsername(),
                tokens.get("accessToken"),
                tokens.get("refreshToken")
        ));
    }

    @Transactional
    @PostMapping("/renewtoken")
    public ResponseEntity<Map<String, String>> renewToken(@RequestBody TokenRequest tokenRequest) {
        // 1. 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(tokenRequest.getRefreshToken())) {
            throw new TokenExpiredException("만료된 리프레시 토큰");
        }

        // 2. DB에서 토큰 조회
        MemberToken memberToken = memberTokenRepository.findByRefreshToken(tokenRequest.getRefreshToken())
                .orElseThrow(() -> new InvalidJwtTokenException("유효하지 않은 토큰"));

        // 3. 7일 만료 확인 (리프레시 토큰 수명)
        LocalDateTime refreshTokenExpiry = memberToken.getCreatedAt().plusDays(7);
        if (LocalDateTime.now().isAfter(refreshTokenExpiry)) {
            memberTokenRepository.delete(memberToken);
            throw new TokenExpiredException("리프레시 토큰 만료. 재로그인이 필요합니다.");
        }

        // 4. 새 액세스 토큰 발급 (리프레시 토큰은 유지)
        String loginId = jwtTokenProvider.getUsername(tokenRequest.getRefreshToken());
        String newAccessToken = jwtTokenProvider.createAccessToken(loginId); // ✅ 변경

        // 5. 토큰 정보 업데이트 (리프레시 토큰 변경 X)
        memberToken.setAccessToken(newAccessToken);
        memberToken.setLastLoginTime(LocalDateTime.now());

        // 6. 저장
        memberTokenRepository.save(memberToken);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "refreshToken", memberToken.getRefreshToken() // 기존 리프레시 토큰 반환 ✅
        ));
    }

    @Transactional
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(required = false) TokenRequest tokenRequest
    ) {
        try {
            // 1. 사용자 조회
            Member member = service.findByLoginId(userDetails.getUsername())
                    .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));

            // 2. 리프레시 토큰이 제공된 경우 해당 토큰만 삭제
            if (tokenRequest != null && tokenRequest.getRefreshToken() != null) {
                memberTokenRepository.findByRefreshToken(tokenRequest.getRefreshToken())
                        .ifPresent(memberTokenRepository::delete);
            } else {
                // 3. 리프레시 토큰이 제공되지 않은 경우 사용자의 모든 토큰 삭제
                memberTokenRepository.deleteByMember(member);
            }

            // 4. 로그아웃 시간 기록
            member.setLastLogoutAt(LocalDateTime.now());
            service.save(member);

            return ResponseEntity.ok(SuccessResponse.success("로그아웃 성공"));

        } catch (Exception e) {
            // 5. 예외가 발생하더라도 토큰 삭제 시도
            try {
                memberTokenRepository.deleteByMember(
                        service.findByLoginId(userDetails.getUsername())
                                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()))
                );
            } catch (Exception ignored) {
                // 토큰 삭제 실패는 무시
            }
            throw e;
        }
    }


    @Transactional
    @DeleteMapping("/delete/{member_id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long member_id,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AccessDeniedException {
        Member member = service.findByLoginId(userDetails.getUsername()).orElseThrow(()->new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        if (!member.getId().equals(member_id)) {
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }
        service.deleteById(member_id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/password/{member_id}")
    public ResponseEntity<SuccessResponse> changePassword(
            @PathVariable Long member_id,
            @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AccessDeniedException {
        Member member = service.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        if (!member.getId().equals(member_id)) {
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorMessage.CURRENT_PASSWORD_NOT_MATCH.getMessage());
        }

        // 새 비밀번호 설정
        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        service.save(member);

        return ResponseEntity.ok(SuccessResponse.success("비밀번호가 변경되었습니다."));
    }

    @Transactional
    @PostMapping("/username/{member_id}")
    public ResponseEntity<SuccessResponse> changeUsername(@PathVariable Long member_id,@AuthenticationPrincipal UserDetails userDetails,@RequestBody UsernameChangeRequest request) throws AccessDeniedException {
        Member member = service.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        if (!member.getId().equals(member_id)) {
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }

        member.setUsername(request.getUsername());
        service.save(member);
        return ResponseEntity.ok(SuccessResponse.success("이름이 변경되었습니다."+request.getUsername()));
    }

    @Transactional
    @GetMapping("/detail/{member_id}")
    public ResponseEntity<MemberResponse> getMemberInfo(
            @PathVariable Long member_id,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AccessDeniedException {
        Member member = service.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        if (!member.getId().equals(member_id)) {
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }
        return ResponseEntity.ok(MemberResponse.fromEntity(member,service.getProfileImageUrl(member.getId())));
    }


    @Transactional
    @GetMapping("/saved/{member_id}")
    public ResponseEntity<Set<FacilityResponse>> getSavedFacility(@PathVariable Long member_id,@AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        Member member = service.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        if (!member.getId().equals(member_id)) {
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }
        Set<FacilityResponse> responses = new HashSet<>();
        for(Facility facility: member.getSavedFacilities()){
            responses.add(FacilityResponse.fromEntity(facility,facilityService.getOpenTime(facility.getId()),facilityService.getCloseTime(facility.getId())));
        }
        return ResponseEntity.ok(responses);
    }

    @Transactional
    @GetMapping("/postedby/{member_id}")
    public ResponseEntity<Set<ReviewResponse>> getReviewByMe(@PathVariable Long member_id,@AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        Member member = service.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        if(!member.getId().equals(member_id)){
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }
        Set<ReviewResponse> responses = new HashSet<>();
        for(Review review: member.getReviews()){
            responses.add(ReviewResponse.fromEntity(review));
        }
        return ResponseEntity.ok(responses);
    }

    @Transactional
    @PostMapping("/profile-image/{userId}")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file  // @RequestBody 대신 @RequestParam 사용
    ) throws IOException {
        Member member = service.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        if(!member.getId().equals(userId)){
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }
        String imageUrl = service.uploadProfileImage(userId, file);
        return ResponseEntity.ok(imageUrl);
    }

    @Transactional
    @GetMapping("/profile-image/{userId}")
    public ResponseEntity<String> getProfileImage(@PathVariable Long userId) {
        String imageUrl = service.getProfileImageUrl(userId);
        return ResponseEntity.ok(imageUrl);
    }

    @Transactional
    @PostMapping("/introduction/{memberId}")
    public ResponseEntity<String> changeIntroduction(@PathVariable Long memberId,@AuthenticationPrincipal UserDetails userDetails,@RequestBody IntroductionRequest request) throws AccessDeniedException {
        Member member = service.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        if(!member.getId().equals(memberId)){
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }
        member.setIntroduction(request.getIntroduction());
        return ResponseEntity.ok(request.getIntroduction());
    }
}