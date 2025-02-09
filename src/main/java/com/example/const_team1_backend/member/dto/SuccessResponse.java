package com.example.const_team1_backend.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {
    private String message;

    public static SuccessResponse success(String message) {
        return new SuccessResponse(message);
    }
}