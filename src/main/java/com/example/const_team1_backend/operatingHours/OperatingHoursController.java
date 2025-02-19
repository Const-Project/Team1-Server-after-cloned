package com.example.const_team1_backend.operatingHours;

import com.example.const_team1_backend.operatingHours.dto.OperatingHoursRequest;
import com.example.const_team1_backend.operatingHours.dto.OperatingHoursResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Transactional
@RestController
@RequestMapping("/v1/operatinghours")
public class OperatingHoursController {
    private final OperatingHoursService service;

    public OperatingHoursController(OperatingHoursService service) {
        this.service = service;
    }


    @GetMapping("/building/{buildingId}")
    public ResponseEntity<OperatingHoursResponse> getBuildingOperatingHours(@PathVariable Long buildingId) {
        return ResponseEntity.ok(service.getBuildingOperatingHours(buildingId));
    }

    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<OperatingHoursResponse> getFacilityOperatingHours(@PathVariable Long facilityId) {
        return ResponseEntity.ok(service.getFacilityOperatingHours(facilityId));
    }

    @PostMapping("/building/{buildingId}")
    public ResponseEntity<OperatingHoursResponse> setBuildingOperatingHours(
            @PathVariable Long buildingId,
            @RequestBody OperatingHoursRequest request) {
        return ResponseEntity.ok(service.setBuildingOperatingHours(buildingId, request));
    }

    @PostMapping("/facility/{facilityId}")
    public ResponseEntity<OperatingHoursResponse> setFacilityOperatingHours(
            @PathVariable Long facilityId,
            @RequestBody OperatingHoursRequest request) {
        return ResponseEntity.ok(service.setFacilityOperatingHours(facilityId, request));
    }
}