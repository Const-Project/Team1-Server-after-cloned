package com.example.const_team1_backend.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long member_id;
    private String accessToken;
    private String refreshToken;

    public static AuthResponse fromTokenAndMemberId(Long id,String accessToken,String refreshToken) {
        return new AuthResponse(id,accessToken,refreshToken);
    }
}