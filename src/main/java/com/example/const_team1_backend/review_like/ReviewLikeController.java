package com.example.const_team1_backend.review_like;

import com.example.const_team1_backend.BaseController;
import com.example.const_team1_backend.common.exception.BadRequestException;
import com.example.const_team1_backend.common.message.ErrorMessage;
import com.example.const_team1_backend.member.Member;
import com.example.const_team1_backend.member.MemberService;
import com.example.const_team1_backend.review.Review;
import com.example.const_team1_backend.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/v1/reviewlikes")
public class ReviewLikeController extends BaseController<ReviewLike,ReviewLikeService> {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MemberService memberService;

    public ReviewLikeController(ReviewLikeService service) {
        super(service);
    }

    @Transactional
    @DeleteMapping("/unlike/{review_id}")
    public ResponseEntity<Void> cancelReviewLike(@PathVariable Long review_id,@AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        Member member = memberService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));
        Long memberId = member.getId();
        if(service.findByMemberIdAndReviewId(memberId,review_id)==null){
            throw new BadRequestException(ErrorMessage.LIKE_NOT_EXIST.getMessage());
        };
        ReviewLike reviewLike = service.findByMemberIdAndReviewId(memberId,review_id);
        Review review = reviewService.findById(review_id);
        review.removeLike(reviewLike);
        member.deleteReviewLike(reviewLike);
        service.deleteById(reviewLike.getId());

        return ResponseEntity.noContent().build();
    }

}
