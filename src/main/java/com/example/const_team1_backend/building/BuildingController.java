package com.example.const_team1_backend.building;

import com.example.const_team1_backend.BaseController;
import com.example.const_team1_backend.building.dto.BuildingResponse;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.facility.FacilityService;
import com.example.const_team1_backend.facility.dto.FacilityResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequestMapping(value = "/v1/buildings",produces = "application/json; charset=UTF-8")
@RestController
public class BuildingController extends BaseController<Building,BuildingService> {

    @Autowired
    private FacilityService facilityService;

    public BuildingController(@Qualifier("buildingService") BuildingService service) {
        super(service);
    }

    @Transactional
    @GetMapping("/all")
    public ResponseEntity<List<BuildingResponse>> getAllBuildings() {
        List<Building> buildings = service.findAll();
        List<BuildingResponse> responses = new ArrayList<>();
        for (Building building : buildings) {
            responses.add(service.getBuildingResponseById(building.getId()));
        }
        return ResponseEntity.ok(responses);
    }

    @Transactional
    @GetMapping("/detail/{building_id}")
    public ResponseEntity<BuildingResponse> getBuildingById(@PathVariable Long building_id) {
        return ResponseEntity.ok(service.getBuildingResponseById(building_id));
    }

    @Transactional
    @GetMapping("/facilities/{id}")
    public ResponseEntity<Set<FacilityResponse>> getFacilities(@PathVariable Long id) {
        Set<Facility> facilities = service.getAllFacilitiesById(id);
        Set<FacilityResponse> responses = new java.util.HashSet<>();
        for (Facility facility : facilities) {
            responses.add(facilityService.getFacilityResponseById(facility.getId()));
        }
        return ResponseEntity.ok(responses);
    }

    @Transactional
    @GetMapping("/floor_facilities/{build_id}/floor/{floor}")
    public ResponseEntity<Set<FacilityResponse>> getBuildingFacilitiesByFloor(@PathVariable Long build_id,@PathVariable int floor) {
        Set<Facility> facilities = service.getFacilitiesByFloor(build_id, floor);
        Set<FacilityResponse> responses = new java.util.HashSet<>();
        for (Facility facility : facilities) {
            responses.add(facilityService.getFacilityResponseById(facility.getId()));
        }
        return ResponseEntity.ok(responses);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBuildingById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}