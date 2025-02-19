package com.example.const_team1_backend.building;

import com.example.const_team1_backend.BaseService;
import com.example.const_team1_backend.building.dto.BuildingResponse;
import com.example.const_team1_backend.common.exception.BadRequestException;
import com.example.const_team1_backend.common.message.ErrorMessage;
import com.example.const_team1_backend.config.s3.S3Service;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.operatingHours.OperatingHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service("buildingService")
public class BuildingService extends BaseService<Building, BuildingRepository> {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private  OperatingHoursService operatingHoursService;

    public BuildingService(BuildingRepository repository) {
        super(repository);
    }

    @Transactional
    public Building findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.BUILDING_NOT_EXIST.getMessage()));
    }


    @Transactional
    public Set<Facility> getAllFacilitiesById(Long id) {
        Building building = findById(id);
        return building.getFacilities();
    }

    @Transactional
    public Set<Facility> getFacilitiesByFloor(Long buildingId, int floor) {
        Building building = findById(buildingId);
        return building.getFacilities().stream()
                .filter(facility -> facility.getFloor() == floor)
                .collect(Collectors.toSet());
    }

    @Transactional
    public BuildingResponse getBuildingResponseById(Long buildingId) {
        Building building = repository.findById(buildingId).orElse(null);
        if (building == null) {
            return null;
        }

        String imageUrl = building.getImageKey() != null ?
                s3Service.getUrl(building.getImageKey()) :
                s3Service.getUrl("R.jpg");

        // 건물의 운영시간 조회
        LocalTime buildingOpenTime = getOpenTime(buildingId);
        LocalTime buildingCloseTime = getCloseTime(buildingId);

        // 각 시설별 운영시간을 미리 조회
        Map<Long, LocalTime> facilityOpenTimes = new HashMap<>();
        Map<Long, LocalTime> facilityCloseTimes = new HashMap<>();

        for (Facility facility : building.getFacilities()) {
            facilityOpenTimes.put(facility.getId(),
                    operatingHoursService.getFacilityOpenTime(facility.getId()));
            facilityCloseTimes.put(facility.getId(),
                    operatingHoursService.getFacilityCloseTime(facility.getId()));
        }

        return BuildingResponse.fromEntity(building, imageUrl, buildingOpenTime, buildingCloseTime,
                facilityOpenTimes, facilityCloseTimes);
    }

    // Add methods to get facility-specific operating hours
    public LocalTime getFacilityOpenTimeFromBuilding(Long facilityId) {
        return operatingHoursService.getFacilityOpenTime(facilityId);
    }

    public LocalTime getFacilityCloseTimeFromBuilding(Long facilityId) {
        return operatingHoursService.getFacilityCloseTime(facilityId);
    }

    @Transactional
    public void deleteById(Long buildingId) {
        Building building = findById(buildingId);
        if (building == null) {
            throw new BadRequestException(ErrorMessage.BUILDING_NOT_EXIST.getMessage());
        }
        repository.deleteById(buildingId);
    }

    public LocalTime getOpenTime(Long buildingId) {
        repository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("해당 건물이 존재하지 않습니다."));
        return operatingHoursService.getBuildingOpenTime(buildingId);
    }

    public LocalTime getCloseTime(Long buildingId) {
        repository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("해당 건물이 존재하지 않습니다."));
        return operatingHoursService.getBuildingCloseTime(buildingId);
    }
}
