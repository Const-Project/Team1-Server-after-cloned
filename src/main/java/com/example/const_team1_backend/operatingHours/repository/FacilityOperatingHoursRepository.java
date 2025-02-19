package com.example.const_team1_backend.operatingHours.repository;

import com.example.const_team1_backend.common.enums.OperatingType;
import com.example.const_team1_backend.operatingHours.entity.FacilityOperatingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface FacilityOperatingHoursRepository extends JpaRepository<FacilityOperatingHours, Long> {
    @Query("SELECT b FROM FacilityOperatingHours b WHERE b.facility.id = :facilityId AND EXISTS (SELECT t FROM b.types t WHERE t IN :possibleTypes)")
    List<FacilityOperatingHours> findByFacilityIdAndPossibleTypes(
            @Param("facilityId") Long facilityId,
            @Param("possibleTypes") Set<OperatingType> possibleTypes
    );

}