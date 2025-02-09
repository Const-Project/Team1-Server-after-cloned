package com.example.const_team1_backend.review_like;

import com.example.const_team1_backend.BaseRepository;
import com.example.const_team1_backend.review.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends BaseRepository<ReviewLike, Long> {
    @Query("SELECT r FROM ReviewLike r WHERE r.member.id = :memberId AND r.review.id = :reviewId")
    Optional<ReviewLike> findByMemberIdAndReviewId( @Param("memberId") Long memberId,@Param("reviewId") Long reviewId);
}
