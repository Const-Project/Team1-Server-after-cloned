package com.example.const_team1_backend.operatingHours.entity;

import com.example.const_team1_backend.BaseEntity;
import com.example.const_team1_backend.common.enums.OperatingType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class OperatingHours extends BaseEntity {


    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<OperatingType> types = new HashSet<>();

    private LocalTime openTime;
    private LocalTime closeTime;

    @Builder
    public OperatingHours(Set<OperatingType> types, LocalTime openTime, LocalTime closeTime) {
        this.types = types;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public void update(LocalTime openTime, LocalTime closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}
