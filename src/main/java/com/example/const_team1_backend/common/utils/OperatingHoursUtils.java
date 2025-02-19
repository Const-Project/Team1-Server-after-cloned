package com.example.const_team1_backend.common.utils;

import com.example.const_team1_backend.common.enums.OperatingType;
import com.example.const_team1_backend.operatingHours.entity.OperatingHours;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.Optional;

@NoArgsConstructor
public class OperatingHoursUtils {

    public static LocalTime getOpenTimeForDate(LocalDate date, List<OperatingHours> operatingHours) {
        Set<OperatingType> types = OperatingType.getTypesForDate(date);
        return findMostSpecificOperatingHours(types, operatingHours)
                .map(OperatingHours::getOpenTime)
                .orElse(null);
    }

    public static LocalTime getCloseTimeForDate(LocalDate date, List<OperatingHours> operatingHours) {
        Set<OperatingType> types = OperatingType.getTypesForDate(date);
        return findMostSpecificOperatingHours(types, operatingHours)
                .map(OperatingHours::getCloseTime)
                .orElse(null);
    }

    /**
     * 주어진 운영 타입들 중에서 가장 구체적인(우선순위가 높은) 운영시간을 찾습니다.
     * 우선순위: 공휴일 > 방학+주말 > 방학+평일 > 학기중+주말 > 학기중+평일
     */
    private static Optional<OperatingHours> findMostSpecificOperatingHours(
            Set<OperatingType> types,
            List<OperatingHours> operatingHours) {

        return operatingHours.stream()
                .filter(oh -> isOperatingHoursApplicable(oh, types))
                .max(Comparator.comparingInt(oh -> calculatePriority(oh.getTypes(), types)));
    }

    /**
     * 주어진 운영시간이 현재 타입들에 적용 가능한지 확인합니다.
     */
    private static boolean isOperatingHoursApplicable(OperatingHours hours, Set<OperatingType> currentTypes) {
        Set<OperatingType> requiredTypes = hours.getTypes();
        return currentTypes.containsAll(requiredTypes);
    }

    /**
     * 운영시간의 우선순위를 계산합니다.
     * 더 구체적인(많은 타입이 매칭되는) 규칙이 높은 우선순위를 가집니다.
     */
    private static int calculatePriority(Set<OperatingType> hoursTypes, Set<OperatingType> currentTypes) {
        int priorityScore = hoursTypes.size(); // 기본적으로 더 많은 타입이 매칭될수록 높은 우선순위

        // 공휴일이 포함된 경우 가장 높은 우선순위
        if (hoursTypes.contains(OperatingType.HOLIDAY)) {
            priorityScore += 100;
        }
        // 방학이 포함된 경우 두 번째로 높은 우선순위
        else if (hoursTypes.contains(OperatingType.VACATION)) {
            priorityScore += 50;
        }

        return priorityScore;
    }
}