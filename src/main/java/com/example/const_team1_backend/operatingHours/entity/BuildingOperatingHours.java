// BuildingOperatingHours.java
package com.example.const_team1_backend.operatingHours.entity;

import com.example.const_team1_backend.building.Building;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuildingOperatingHours extends OperatingHours {
    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

}