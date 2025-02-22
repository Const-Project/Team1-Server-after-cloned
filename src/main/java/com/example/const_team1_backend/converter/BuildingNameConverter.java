package com.example.const_team1_backend.converter;

import java.util.Map;

public class BuildingNameConverter {
    private static Map<String, String> map = Map.ofEntries(
            Map.entry("R", "홍문관"),
            Map.entry("M", "체육관"),
            Map.entry("K", "제1공학관"),
            Map.entry("Playground", "대운동장"),
            Map.entry("J", "제3공학관"),
            Map.entry("L", "와우관"),
            Map.entry("I", "과학관"),
            Map.entry("G", "학생회관"),
            Map.entry("H", "중앙도서관"),
            Map.entry("P", "제2공학관"),
            Map.entry("Q", "정보통신센터"),
            Map.entry("MH", "문헌관"),
            Map.entry("F", "미술학관"),
            Map.entry("E", "조형관"),
            Map.entry("U", "미술종합강의동"),
            Map.entry("B", "인문사회관"),
            Map.entry("Z4", "제4강의동"),
            Map.entry("Z1", "제1강의동"),
            Map.entry("S", "강당"),
            Map.entry("T", "제4공학관"),
            Map.entry("Z3", "제3강의동"),
            Map.entry("Z2", "이천득관(제2강의동)"), // 중복된 키 "Z2"를 하나만 유지
            Map.entry("A", "인문사회관 A동"),
            Map.entry("SecondDormitory", "제2기숙사"),
            Map.entry("C", "인문사회관 C동"),
            Map.entry("D", "인문사회관 D동"),
            Map.entry("V", "국제교육관"),
            Map.entry("W", "남문관"),
            Map.entry("X", "제1기숙사"),
            Map.entry("YM", "예문관"),
            Map.entry("Foreign", "외국인숙소"),
            Map.entry("N", "사회교육원")
    );



    public static String convertName(String name) {
        return map.get(name);
    }
}
