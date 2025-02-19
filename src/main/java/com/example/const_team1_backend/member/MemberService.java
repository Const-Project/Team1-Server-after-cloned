package com.example.const_team1_backend.member;

import com.example.const_team1_backend.BaseService;
import com.example.const_team1_backend.config.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service("memberService")
public class MemberService extends BaseService<Member,MemberRepository> {

    private final S3Service s3Service;

    public MemberService(MemberRepository repository, S3Service s3Service) {  // 생성자 파라미터에 S3Service 추가
        super(repository);
        this.s3Service = s3Service;  // 생성자에서 주입
    }

    @Transactional
    public Optional<Member> findByLoginId(String loginId) {
        return repository.findByLoginId(loginId);
    }


    @Transactional
    public void createMember(Member member) {
        repository.save(member);
    }

    @Transactional
    public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        Member user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String imageUrl = s3Service.uploadFile(file, "profile-images", user.getLoginId());
        user.setProfileImageUrl(imageUrl);
        repository.save(user);

        return imageUrl;
    }

    public String getProfileImageUrl(Long userId) {
        Member user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getProfileImageUrl();
    }

    @Transactional
    public void deleteById(Long userId) {
        repository.deleteById(userId);
    }
}
