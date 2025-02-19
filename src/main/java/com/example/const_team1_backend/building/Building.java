package com.example.const_team1_backend.building;

import com.example.const_team1_backend.BaseEntity;
import com.example.const_team1_backend.common.utils.OperatingHoursUtils;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.operatingHours.entity.BuildingOperatingHours;
import com.example.const_team1_backend.operatingHours.entity.OperatingHours;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Building extends BaseEntity {

    private String imageKey;

    private String name;

    private int highestFloor;

    private int lowestFloor;

    private Double latitude;

    private Double longitude;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<BuildingOperatingHours> operatingHours;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonManagedReference
    private Set<Facility> facilities;

    public int getActualTotalFloors() {
        if(lowestFloor>0) return highestFloor-lowestFloor+1;
        return highestFloor - lowestFloor;
    }

    public LocalTime getOpenTime(){
        List<OperatingHours> operatingHoursSet = List.copyOf(operatingHours);
        return OperatingHoursUtils.getOpenTimeForDate(LocalDate.now(),operatingHoursSet);
    }

    public LocalTime getCloseTime(){
        List<OperatingHours> operatingHoursSet = List.copyOf(operatingHours);
        return OperatingHoursUtils.getCloseTimeForDate(LocalDate.now(),operatingHoursSet);
    }

}
