package com.example.const_team1_backend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {
    private String loginId;
    private String username;
    private String password;
}
