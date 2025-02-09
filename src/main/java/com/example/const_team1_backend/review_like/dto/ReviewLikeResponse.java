package com.example.const_team1_backend.review_like.dto;

import com.example.const_team1_backend.review_like.ReviewLike;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikeResponse {
    private Long LikeId;
    private LocalDateTime createdAt;

    public static ReviewLikeResponse fromEntity(ReviewLike reviewLike) {
        return new ReviewLikeResponse(
                reviewLike.getId(),
                reviewLike.getCreatedAt() != null ? LocalDateTime.parse(reviewLike.getCreatedAt().toString()) : null
        );
    }
}

