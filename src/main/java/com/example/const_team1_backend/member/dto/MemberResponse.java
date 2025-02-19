package com.example.const_team1_backend.member.dto;

import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.facility.dto.FacilityResponse;
import com.example.const_team1_backend.member.Member;
import com.example.const_team1_backend.review.dto.ReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long memberId;
    private String username;
    private String introduction;
    private String profileImageUrl;
    private String loginId;
    private Long totalSavedFacilities;
    private Set<FacilityResponse> savedFacilities;


    public static MemberResponse fromEntity(Member member,String profileImageUrl) {
        Set<FacilityResponse> savedFacilities = new HashSet<>();
        for(Facility facility : member.getSavedFacilities()){
            savedFacilities.add(new FacilityResponse(
                    facility.getId(),
                    facility.getName(),
                    facility.getCategory().getId(),
                    facility.getFloor(),
                    facility.getTotalLikes(),
                    facility.getTotalDislikes(),
                    facility.getTotalReviews(),
                    facility.getOpenTime()!=null?facility.getOpenTime():facility.getBuilding().getOpenTime(),
                    facility.getCloseTime()!=null?facility.getCloseTime():facility.getBuilding().getCloseTime(),
                    facility.getReviews().stream()
                            .map(ReviewResponse::fromEntity) // 리뷰 엔티티를 ReviewResponse로 변환
                            .collect(Collectors.toSet())
            ));
        }
        return new MemberResponse(
                member.getId(),
                member.getUsername(),
                member.getIntroduction(),
                profileImageUrl,
                member.getLoginId(),
                member.getTotalSavedFacilities(),
                savedFacilities
        );
    }
}