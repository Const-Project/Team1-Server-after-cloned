package com.example.const_team1_backend.building.dto;

import com.example.const_team1_backend.building.Building;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.facility.dto.FacilityResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingResponse {
    private Long buildingId;
    private String name;
    private String imageUrl;
    private int totalFloors;
    private Double latitude;
    private Double longitude;
    private List<Integer> floors;
    private Set<FacilityResponse> facilitySet;


    public static BuildingResponse fromEntity(Building building, String imageUrl) {
        List<Integer> floors = IntStream.rangeClosed(building.getLowestFloor(), building.getHighestFloor())
                .boxed()
                .collect(Collectors.toList());
        if(floors.contains(0)) floors.remove((Integer) 0);
        Set<FacilityResponse> facilitySet = new HashSet<>();
        for (Facility facility : building.getFacilities()) {
            facilitySet.add(FacilityResponse.fromEntity(facility));
        }
        return BuildingResponse.builder()
                .buildingId(building.getId())
                .name(building.getName())
                .imageUrl(imageUrl)
                .totalFloors(building.getActualTotalFloors())
                .latitude(building.getLatitude())  // 직접 필드 사용
                .longitude(building.getLongitude()) // 직접 필드 사용
                .floors(floors)
                .facilitySet(facilitySet)
                .build();
    }
}