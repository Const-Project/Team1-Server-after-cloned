package com.example.const_team1_backend.operatingHours;

import com.example.const_team1_backend.building.Building;
import com.example.const_team1_backend.building.BuildingRepository;
import com.example.const_team1_backend.common.enums.OperatingType;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.facility.FacilityRepository;
import com.example.const_team1_backend.operatingHours.dto.OperatingHoursRequest;
import com.example.const_team1_backend.operatingHours.dto.OperatingHoursResponse;
import com.example.const_team1_backend.operatingHours.entity.BuildingOperatingHours;
import com.example.const_team1_backend.operatingHours.entity.FacilityOperatingHours;
import com.example.const_team1_backend.operatingHours.entity.OperatingHours;
import com.example.const_team1_backend.operatingHours.repository.BuildingOperatingHoursRepository;
import com.example.const_team1_backend.operatingHours.repository.FacilityOperatingHoursRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHoursService {

    @Autowired
    private BuildingOperatingHoursRepository bohRepository;

    @Autowired
    private FacilityOperatingHoursRepository fohRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    public OperatingHoursResponse getBuildingOperatingHours(Long buildingId) {
        LocalDate today = LocalDate.now();
        Set<OperatingType> currentTypes = OperatingType.getTypesForDate(today);

        Set<OperatingType> possibleTypes = flattenTypeSets(generatePossibleTypeSets(currentTypes));


        List<BuildingOperatingHours> operatingHoursList =
                bohRepository.findByBuildingIdAndPossibleTypes(buildingId, possibleTypes);

        if (operatingHoursList.isEmpty()) {
//            throw new IllegalArgumentException("해당 건물의 운영 시간이 존재하지 않습니다.");
            return null;
        }

        return OperatingHoursResponse.from(findMostSpecificHours(operatingHoursList, currentTypes));
    }

    public OperatingHoursResponse getFacilityOperatingHours(Long facilityId) {
        LocalDate today = LocalDate.now();
        Set<OperatingType> currentTypes = OperatingType.getTypesForDate(today);

        Set<OperatingType> possibleTypes = flattenTypeSets(generatePossibleTypeSets(currentTypes));

        List<FacilityOperatingHours> operatingHoursList =
                fohRepository.findByFacilityIdAndPossibleTypes(facilityId, possibleTypes);

        if (operatingHoursList.isEmpty()) {
//            throw new IllegalArgumentException("해당 시설의 운영 시간이 존재하지 않습니다.");
            return null;
        }

        return OperatingHoursResponse.from(findMostSpecificHours(operatingHoursList, currentTypes));
    }

    private Set<Set<OperatingType>> generatePossibleTypeSets(Set<OperatingType> types) {
        Set<Set<OperatingType>> result = new HashSet<>();
        List<OperatingType> typesList = new ArrayList<>(types);

        for (int i = 1; i <= typesList.size(); i++) {
            generateCombinations(typesList, i, 0, new HashSet<>(), result);
        }
        return result;
    }

    private void generateCombinations(List<OperatingType> types, int k, int start,
                                      Set<OperatingType> current,
                                      Set<Set<OperatingType>> result) {
        if (current.size() == k) {
            result.add(new HashSet<>(current));
            return;
        }

        for (int i = start; i < types.size(); i++) {
            current.add(types.get(i));
            generateCombinations(types, k, i + 1, current, result);
            current.remove(types.get(i));
        }
    }

    private <T extends OperatingHours> T findMostSpecificHours(
            List<T> operatingHoursList,
            Set<OperatingType> currentTypes) {
        return operatingHoursList.stream()
                .filter(hours -> isApplicable(hours.getTypes(), currentTypes))
                .max(this::compareOperatingHours)
                .orElse(null);
    }

    private boolean isApplicable(Set<OperatingType> hoursTypes, Set<OperatingType> currentTypes) {
        return currentTypes.containsAll(hoursTypes);
    }

    private int compareOperatingHours(OperatingHours hours1, OperatingHours hours2) {
        if (hours1.getTypes().contains(OperatingType.HOLIDAY) && !hours2.getTypes().contains(OperatingType.HOLIDAY)) {
            return 1;
        }
        if (!hours1.getTypes().contains(OperatingType.HOLIDAY) && hours2.getTypes().contains(OperatingType.HOLIDAY)) {
            return -1;
        }

        if (hours1.getTypes().contains(OperatingType.VACATION) && !hours2.getTypes().contains(OperatingType.VACATION)) {
            return 1;
        }
        if (!hours1.getTypes().contains(OperatingType.VACATION) && hours2.getTypes().contains(OperatingType.VACATION)) {
            return -1;
        }

        return Integer.compare(hours1.getTypes().size(), hours2.getTypes().size());
    }

    private Set<OperatingType> flattenTypeSets(Set<Set<OperatingType>> nestedSets) {
        Set<OperatingType> flattened = new HashSet<>();
        for (Set<OperatingType> set : nestedSets) {
            flattened.addAll(set);
        }
        return flattened;
    }

    public LocalTime getBuildingOpenTime(Long buildingId) {
        LocalDate today = LocalDate.now();
        Set<OperatingType> currentTypes = OperatingType.getTypesForDate(today);
        Set<OperatingType> possibleTypes = flattenTypeSets(generatePossibleTypeSets(currentTypes));
        List<BuildingOperatingHours> operatingHoursList =
                bohRepository.findByBuildingIdAndPossibleTypes(buildingId, possibleTypes);

        if (operatingHoursList.isEmpty()) {
//            throw new IllegalArgumentException("해당 건물의 운영 시간이 존재하지 않습니다.");
            return null;
        }
        if(findMostSpecificHours(operatingHoursList,currentTypes)==null) return null;
        BuildingOperatingHours mostSpecificHours = findMostSpecificHours(operatingHoursList, currentTypes);
        return mostSpecificHours.getOpenTime();
    }

    public LocalTime getFacilityOpenTime(Long facilityId) {
        LocalDate today = LocalDate.now();
        Set<OperatingType> currentTypes = OperatingType.getTypesForDate(today);
        Set<OperatingType> possibleTypes = flattenTypeSets(generatePossibleTypeSets(currentTypes));
        List<FacilityOperatingHours> operatingHoursList =
                fohRepository.findByFacilityIdAndPossibleTypes(facilityId, possibleTypes);

        if (operatingHoursList.isEmpty()) {
//            throw new IllegalArgumentException("해당 시설의 운영 시간이 존재하지 않습니다.");
            return null;
        }
        if(findMostSpecificHours(operatingHoursList,currentTypes)==null) return null;
        FacilityOperatingHours mostSpecificHours = findMostSpecificHours(operatingHoursList, currentTypes);
        return mostSpecificHours.getOpenTime();
    }

    public LocalTime getBuildingCloseTime(Long buildingId) {
        LocalDate today = LocalDate.now();
        Set<OperatingType> currentTypes = OperatingType.getTypesForDate(today);
        Set<OperatingType> possibleTypes = flattenTypeSets(generatePossibleTypeSets(currentTypes));

        List<BuildingOperatingHours> operatingHoursList =
                bohRepository.findByBuildingIdAndPossibleTypes(buildingId, possibleTypes);

        if (operatingHoursList.isEmpty()) {
//            throw new IllegalArgumentException("해당 건물의 운영 시간이 존재하지 않습니다.");
            return null;
        }
        if(findMostSpecificHours(operatingHoursList,currentTypes)==null) return null;
        BuildingOperatingHours mostSpecificHours = findMostSpecificHours(operatingHoursList, currentTypes);
        return mostSpecificHours.getCloseTime();
    }

    public LocalTime getFacilityCloseTime(Long facilityId) {
        LocalDate today = LocalDate.now();
        Set<OperatingType> currentTypes = OperatingType.getTypesForDate(today);
        Set<OperatingType> possibleTypes = flattenTypeSets(generatePossibleTypeSets(currentTypes));
        List<FacilityOperatingHours> operatingHoursList =
                fohRepository.findByFacilityIdAndPossibleTypes(facilityId, possibleTypes);

        if (operatingHoursList.isEmpty()) {
//            throw new IllegalArgumentException("해당 시설의 운영 시간이 존재하지 않습니다.");
            return null;
        }
        if(findMostSpecificHours(operatingHoursList,currentTypes)==null) return null;
        FacilityOperatingHours mostSpecificHours = findMostSpecificHours(operatingHoursList, currentTypes);
        return mostSpecificHours.getCloseTime();
    }



        @Transactional
        public OperatingHoursResponse setBuildingOperatingHours(Long buildingId, OperatingHoursRequest request) {
            Building building = buildingRepository.findById(buildingId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 건물이 존재하지 않습니다."));

            // 같은 타입 조합의 운영시간이 이미 존재하는지 확인
            List<BuildingOperatingHours> existingHours = bohRepository
                    .findByBuildingIdAndPossibleTypes(buildingId, request.getTypes());

            BuildingOperatingHours operatingHours;

            if (!existingHours.isEmpty()) {
                // 이미 존재하는 경우 업데이트
                operatingHours = existingHours.get(0);
                operatingHours.update(request.getOpenTime(), request.getCloseTime());
            } else {
                // 새로 생성
                operatingHours = new BuildingOperatingHours();
                operatingHours.setBuilding(building);
                operatingHours.setTypes(request.getTypes());
                operatingHours.update(request.getOpenTime(), request.getCloseTime());
                bohRepository.save(operatingHours);
            }

            return OperatingHoursResponse.from(operatingHours);
        }

        @Transactional
        public OperatingHoursResponse setFacilityOperatingHours(Long facilityId, OperatingHoursRequest request) {
            Facility facility = facilityRepository.findById(facilityId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 시설이 존재하지 않습니다."));

            // 같은 타입 조합의 운영시간이 이미 존재하는지 확인
            List<FacilityOperatingHours> existingHours = fohRepository
                    .findByFacilityIdAndPossibleTypes(facilityId, request.getTypes());

            FacilityOperatingHours operatingHours;

            if (!existingHours.isEmpty()) {
                // 이미 존재하는 경우 업데이트
                operatingHours = existingHours.get(0);
                operatingHours.update(request.getOpenTime(), request.getCloseTime());
            } else {
                // 새로 생성
                operatingHours = new FacilityOperatingHours();
                operatingHours.setFacility(facility);
                operatingHours.setTypes(request.getTypes());
                operatingHours.update(request.getOpenTime(), request.getCloseTime());
                fohRepository.save(operatingHours);
            }


            return OperatingHoursResponse.from(operatingHours);
        }
}