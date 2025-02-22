package com.example.const_team1_backend.converter;

import java.util.Map;

public class FacilityNameConverter {
    private static Map<String,String> map = Map.ofEntries(
            Map.entry("Cafe Namu","카페나무"),
            Map.entry("Grazie","그라찌에"),
            Map.entry("Shinhan Bank","신한은행")
    );

    public static String convertName(String name) {
        return map.get(name);
    }
}
