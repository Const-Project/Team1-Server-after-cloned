package com.example.const_team1_backend.operatingHours.dto;

import com.example.const_team1_backend.common.enums.OperatingType;
import com.example.const_team1_backend.operatingHours.entity.BuildingOperatingHours;
import com.example.const_team1_backend.operatingHours.entity.FacilityOperatingHours;
import com.example.const_team1_backend.operatingHours.entity.OperatingHours;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Set;

@Getter
@Builder
public class OperatingHoursResponse {
    private Set<OperatingType> types;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isCurrentlyOpen;
    private String name;  // 건물명 또는 시설명


    public static OperatingHoursResponse from(OperatingHours operatingHours) {
        LocalTime now = LocalTime.now();
        boolean isOpen = now.isAfter(operatingHours.getOpenTime()) &&
                now.isBefore(operatingHours.getCloseTime());

        String name = "";
        if (operatingHours instanceof BuildingOperatingHours) {
            name = ((BuildingOperatingHours) operatingHours).getBuilding().getName();
        } else if (operatingHours instanceof FacilityOperatingHours) {
            name = ((FacilityOperatingHours) operatingHours).getFacility().getName();
        }

        return OperatingHoursResponse.builder()
                .types(operatingHours.getTypes())
                .openTime(operatingHours.getOpenTime())
                .closeTime(operatingHours.getCloseTime())
                .isCurrentlyOpen(isOpen)
                .name(name)
                .build();
    }
}