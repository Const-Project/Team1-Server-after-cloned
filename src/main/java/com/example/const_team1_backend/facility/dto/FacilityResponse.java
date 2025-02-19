package com.example.const_team1_backend.facility.dto;

import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.review.dto.ReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityResponse { // 시설 id
    private Long facilityId;
    private String name; // 시설 이름
    private Long categoryId;
    private int floor; // 위치한 층
    private Long totalLikes;
    private Long totalDislikes;
    private Long totalReviews;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Set<ReviewResponse> reviewSet; // 리뷰 배열


    public static FacilityResponse fromEntity(Facility facility,LocalTime openTime, LocalTime closeTime) {
        return new FacilityResponse(
                facility.getId(),
                facility.getName(),
                facility.getCategory().getId(),
                facility.getFloor(),
                facility.getTotalLikes(),
                facility.getTotalDislikes(),
                facility.getTotalReviews(),
                openTime,
                closeTime,
                facility.getReviews().stream()
                        .map(ReviewResponse::fromEntity) // 리뷰 엔티티를 ReviewResponse로 변환
                        .collect(Collectors.toSet())
        );
    }

}