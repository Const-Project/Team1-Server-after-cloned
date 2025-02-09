package com.example.const_team1_backend.review;

import com.example.const_team1_backend.BaseController;
import com.example.const_team1_backend.common.exception.BadRequestException;
import com.example.const_team1_backend.common.message.ErrorMessage;
import com.example.const_team1_backend.member.Member;
import com.example.const_team1_backend.member.MemberService;
import com.example.const_team1_backend.review.dto.ReviewRequest;
import com.example.const_team1_backend.review_like.ReviewLike;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@RestController
@RequestMapping(value = "/v1/reviews",produces = "application/json; charset=UTF-8")
public class ReviewController extends BaseController<Review, ReviewService> {

    private final MemberService memberService;

    public ReviewController(ReviewService service, MemberService memberService) {
        super(service);
        this.memberService = memberService;
    }

    @Transactional
    @DeleteMapping("/delete/{review_id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long review_id,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AccessDeniedException {
        Member member = memberService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        Review review = service.findById(review_id);
        if(!Objects.equals(review.getMember().getId(), member.getId())){
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }

        service.deleteById(review_id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/like/{review_id}")
    public ResponseEntity<Void> likeReview(
            @PathVariable Long review_id,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AccessDeniedException {
        Member member = memberService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        ReviewLike reviewLike = new ReviewLike();
        Review review = service.findById(review_id);
        if(Objects.equals(review.getMember().getId(), member.getId())){
            throw new AccessDeniedException(ErrorMessage.CANNOT_LIKE_MINE.getMessage());
        }

        member.addReviewLike(reviewLike);
        review.addLike(reviewLike);
        reviewLike.setReview(review);
        reviewLike.setMember(member);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PatchMapping("/patch/{review_id}")
    public ResponseEntity<Void> patchReview(@PathVariable Long review_id,
                                            @AuthenticationPrincipal UserDetails userDetails, @RequestBody ReviewRequest request) throws AccessDeniedException {
        Member member = memberService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        Review review = service.findById(review_id);
        if(!Objects.equals(review.getMember().getId(), member.getId())){
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }
        review.setContent(request.getContent());
        return ResponseEntity.ok().build();
    }
}