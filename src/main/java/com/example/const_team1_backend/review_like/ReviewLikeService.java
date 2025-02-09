package com.example.const_team1_backend.review_like;

import com.example.const_team1_backend.BaseService;
import com.example.const_team1_backend.review.Review;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service("reviewLikeService")
public class ReviewLikeService extends BaseService<ReviewLike,ReviewLikeRepository> {
    public ReviewLikeService(ReviewLikeRepository repository) {
        super(repository);
    }

    public ReviewLike findByMemberIdAndReviewId(Long memberId, Long id) throws AccessDeniedException {
        Optional<ReviewLike> reviewLike = repository.findByMemberIdAndReviewId(memberId, id);
        return reviewLike.orElse(null);
    }
}
