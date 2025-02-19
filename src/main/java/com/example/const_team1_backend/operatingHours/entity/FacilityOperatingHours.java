package com.example.const_team1_backend.operatingHours.entity;

import com.example.const_team1_backend.facility.Facility;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FacilityOperatingHours extends OperatingHours {
    
    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facility facility;

}
