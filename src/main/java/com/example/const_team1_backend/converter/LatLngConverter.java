package com.example.const_team1_backend.converter;

import com.google.maps.model.LatLng;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LatLngConverter implements AttributeConverter<LatLng, String> {

    @Override
    public String convertToDatabaseColumn(LatLng latLng) {
        if (latLng == null) {
            return null;
        }
        // LatLng를 문자열로 변환 (예: "latitude,longitude")
        return latLng.lat + "," + latLng.lng;
    }

    @Override
    public LatLng convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        // 문자열을 LatLng로 변환 (예: "latitude,longitude" -> LatLng)
        String[] parts = dbData.split(",");
        double lat = Double.parseDouble(parts[0]);
        double lng = Double.parseDouble(parts[1]);
        return new LatLng(lat, lng);
    }
}
