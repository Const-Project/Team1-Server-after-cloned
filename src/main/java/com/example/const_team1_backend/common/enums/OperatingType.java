package com.example.const_team1_backend.common.enums;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum OperatingType {
    WEEKDAY("평일") {
        @Override
        public boolean isApplicable(LocalDate date) {
            DayOfWeek day = date.getDayOfWeek();
            return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && !HOLIDAY.isApplicable(date);
        }
    },
    WEEKEND("주말") {
        @Override
        public boolean isApplicable(LocalDate date) {
            DayOfWeek day = date.getDayOfWeek();
            return (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) && !HOLIDAY.isApplicable(date);
        }
    },
    HOLIDAY("공휴일") {
        private final List<LocalDate> holidays = Arrays.asList(
                LocalDate.of(LocalDate.now().getYear(), 1, 1),   // 신정
                LocalDate.of(LocalDate.now().getYear(), 3, 1),   // 삼일절
                LocalDate.of(LocalDate.now().getYear(), 5, 5),   // 어린이날
                LocalDate.of(LocalDate.now().getYear(), 6, 6),   // 현충일
                LocalDate.of(LocalDate.now().getYear(), 8, 15),  // 광복절
                LocalDate.of(LocalDate.now().getYear(), 10, 3),  // 개천절
                LocalDate.of(LocalDate.now().getYear(), 10, 9),  // 한글날
                LocalDate.of(LocalDate.now().getYear(), 12, 25)  // 크리스마스
        );

        @Override
        public boolean isApplicable(LocalDate date) {
            return holidays.stream()
                    .anyMatch(holiday -> holiday.equals(date));
        }
    },
    VACATION("방학") {
        @Override
        public boolean isApplicable(LocalDate date) {
            Month month = date.getMonth();
            return (month == Month.JUNE || month == Month.JULY || month == Month.AUGUST ||  // 여름방학
                    month == Month.DECEMBER || month == Month.JANUARY || month == Month.FEBRUARY); // 겨울방학
        }
    },
    SEMESTER("학기중") {
        @Override
        public boolean isApplicable(LocalDate date) {
            return !VACATION.isApplicable(date);
        }
    };

    private final String description;

    OperatingType(String description) {
        this.description = description;
    }

    public abstract boolean isApplicable(LocalDate date);

    public static Set<OperatingType> getTypesForDate(LocalDate date) {
        return Arrays.stream(OperatingType.values())
                .filter(type -> type.isApplicable(date))
                .collect(Collectors.toSet());
    }

    // 특정 조합의 Type들이 모두 적용되는지 확인하는 메소드
    public static boolean isApplicableTypes(LocalDate date, OperatingType... types) {
        Set<OperatingType> applicableTypes = getTypesForDate(date);
        return Arrays.stream(types)
                .allMatch(applicableTypes::contains);
    }
}