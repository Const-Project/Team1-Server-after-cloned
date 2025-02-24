package com.example.const_team1_backend.review.dto;

import com.example.const_team1_backend.review.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ReviewResponse {
    private Long reviewId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    private Long totalLikes;
    private boolean isOwner;

    public static ReviewResponse fromEntity(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getContent(),
                review.getMember().getUsername(),
                review.getCreatedAt() == null ? LocalDateTime.now() : review.getCreatedAt(),
                review.getTotalLikes(),
                true
        );
    }
}
