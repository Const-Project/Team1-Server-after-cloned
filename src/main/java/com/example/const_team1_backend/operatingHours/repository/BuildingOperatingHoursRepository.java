package com.example.const_team1_backend.operatingHours.repository;

import com.example.const_team1_backend.common.enums.OperatingType;
import com.example.const_team1_backend.operatingHours.entity.BuildingOperatingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BuildingOperatingHoursRepository extends JpaRepository<BuildingOperatingHours, Long> {
    @Query("SELECT b FROM BuildingOperatingHours b WHERE b.building.id = :buildingId AND EXISTS (SELECT t FROM b.types t WHERE t IN :possibleTypes)")
    List<BuildingOperatingHours> findByBuildingIdAndPossibleTypes(
            @Param("buildingId") Long buildingId,
            @Param("possibleTypes") Set<OperatingType> possibleTypes
    );



}