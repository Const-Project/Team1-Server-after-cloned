package com.example.const_team1_backend.operatingHours.dto;

import com.example.const_team1_backend.common.enums.OperatingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHoursRequest {
    private Set<OperatingType> types;
    private LocalTime openTime;
    private LocalTime closeTime;
}
