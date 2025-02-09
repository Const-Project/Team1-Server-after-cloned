package com.example.const_team1_backend.location;

import com.example.const_team1_backend.converter.LatLngConverter;
import com.google.maps.model.LatLng;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Location {
    @Convert(converter = LatLngConverter.class)
    private LatLng latLng;

    public Double getLatitude() {
        return latLng != null ? latLng.lat : 37.55260;
    }

    public Double getLongitude() {
        return latLng != null ? latLng.lng : 126.9249;
    }
}
