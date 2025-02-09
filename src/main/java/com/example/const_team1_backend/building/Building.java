package com.example.const_team1_backend.building;

import com.example.const_team1_backend.BaseEntity;
import com.example.const_team1_backend.facility.Facility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;

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

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonManagedReference
    private Set<Facility> facilities;

    public int getActualTotalFloors() {
        if(lowestFloor>0) return highestFloor-lowestFloor+1;
        return highestFloor - lowestFloor;
    }
}
