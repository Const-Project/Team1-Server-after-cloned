package com.example.const_team1_backend.facility;

import com.example.const_team1_backend.BaseController;
import com.example.const_team1_backend.common.exception.BadRequestException;
import com.example.const_team1_backend.common.message.ErrorMessage;
import com.example.const_team1_backend.facility.dto.FacilityResponse;
import com.example.const_team1_backend.member.Member;
import com.example.const_team1_backend.member.MemberService;
import com.example.const_team1_backend.reaction.Reaction;
import com.example.const_team1_backend.reaction.ReactionService;
import com.example.const_team1_backend.reaction.dto.ReactionRequest;
import com.example.const_team1_backend.reaction.dto.ReactionResponse;
import com.example.const_team1_backend.review.Review;
import com.example.const_team1_backend.review.ReviewService;
import com.example.const_team1_backend.review.dto.ReviewRequest;
import com.example.const_team1_backend.review.dto.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/v1/facilities",produces = "application/json; charset=UTF-8")
public class FacilityController extends BaseController<Facility,FacilityService> {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private ReviewService reviewService;

    public FacilityController(FacilityService service) {
        super(service);
    }

    @Transactional
    @GetMapping("/all")
    public ResponseEntity<List<FacilityResponse>> getAllFacilities() {
        List<FacilityResponse> responses = new ArrayList<>();
        for (Facility facility : service.findAll()) {
            responses.add(service.getFacilityResponseById(facility.getId()));
        }
        return ResponseEntity.ok(responses);
    }


    @Transactional
    @GetMapping("/detail/{facil_id}")
    public ResponseEntity<FacilityResponse> getFacilityById(@PathVariable Long facil_id) {
        return ResponseEntity.ok(service.getFacilityResponseById(facil_id));
    }

    @Transactional
    @GetMapping("/floor/{floor}")
    public ResponseEntity<Set<FacilityResponse>> getFacilitiesByFloor(@PathVariable Long floor) {
        return ResponseEntity.ok(service.getFacilityResponsesByFloor(floor));
    }

    @Transactional
    @GetMapping("/category/{category}")
    public ResponseEntity<Set<FacilityResponse>> getFacilitiesByCategory(@PathVariable Long category) {
        return ResponseEntity.ok(service.getFacilityResponsesByCategory(category));
    }

    @Transactional
    @GetMapping("/reviews/{id}")
    public ResponseEntity<Set<ReviewResponse>> getReviews(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Set<Review> reviews = service.getAllReviewsById(id);
        Set<ReviewResponse> responseSet = new LinkedHashSet<>(); // 순서가 유지되는 Set

        // 로그인한 경우, 사용자의 리뷰를 먼저 추가
        if (userDetails != null) {
            String ID = userDetails.getUsername();
            memberService.findByLoginId(ID).ifPresent(member -> reviews.stream()
                    .filter(review -> review.getMember().getId().equals(member.getId()))
                    .map(review -> {
                        ReviewResponse response = ReviewResponse.fromEntity(review);
                        response.setOwner(true); // ReviewResponse에 isOwner 필드 추가 필요
                        return response;
                    })
                    .forEach(responseSet::add));

        }

        // 나머지 리뷰들 추가
        reviews.stream()
                .filter(review -> {
                    if (userDetails == null) return true;
                    Member member = memberService.findByLoginId(userDetails.getUsername()).orElse(null);
                    return member == null || !review.getMember().getId().equals(member.getId());
                })
                .map(ReviewResponse::fromEntity)
                .forEach(responseSet::add);

        return ResponseEntity.ok(responseSet);
    }

    @Transactional
    @PostMapping("/save/{id}")
    public ResponseEntity<Void> saveFacility(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Member member = memberService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        Facility facility = service.findById(id);

        if(member.getSavedFacilities().contains(facility)) {
            throw new BadRequestException(ErrorMessage.ALREADY_SAVED_FACILITY.getMessage());
        }

        member.saveFacility(facility);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/deletesaved/{id}")
    public ResponseEntity<Void> deleteSavedFacility(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        Facility facility = service.findById(id);
        if(member==null) {throw new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage());}
        if(facility==null||!member.getSavedFacilities().contains(facility)) {
            throw new BadRequestException(ErrorMessage.NOT_SAVED_FACILITY.getMessage());
        }
        member.deleteFacility(facility);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/react/{facility_id}")
    public ResponseEntity<ReactionResponse> createReaction(
            @PathVariable Long facility_id,
            @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Member member = memberService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        Facility facility = service.findById(facility_id);
        Optional<Reaction> reactionOptional = (reactionService.findByFacilityIdAndMemberId(facility_id, member.getId()));
        if(reactionOptional.isPresent()){
            Reaction reaction = reactionOptional.get();
            if(reactionOptional.get().getType()==request.getType()){
                throw new BadRequestException(ErrorMessage.ALREADY_REACTED_FACILITY.getMessage());
            }
            reaction.setType(request.getType());
            return ResponseEntity.ok().body(ReactionResponse.fromEntity(reaction));
        }
        Reaction reaction = new Reaction(request.getType());
        member.addReaction(reaction);
        facility.addReaction(reaction);
        Reaction savedReaction = reactionService.save(reaction);

        return ResponseEntity.ok(ReactionResponse.fromEntity(savedReaction));
    }

    @Transactional
    @PostMapping("/postreview/{facility_id}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long facility_id,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Member member = memberService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        Facility facility = service.findById(facility_id);
        Review review = new Review();
        review.setContent(request.getContent());
        member.addReview(review);
        facility.addReviews(review);

        Review savedReview = reviewService.save(review);
        return ResponseEntity.ok(ReviewResponse.fromEntity(savedReview));
    }
}
