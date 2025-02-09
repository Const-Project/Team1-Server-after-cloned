package com.example.const_team1_backend.common.message;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    BUILDING_NOT_EXIST("건물을 찾을 수 없습니다."),
    FACILITY_NOT_EXIST("시설을 찾을 수 없습니다."),
    INVALID_CREDENTIALS("이메일 또는 비밀번호가 틀렸습니다."),
    NOT_SIGNED("로그인되지 않은 유저입니다."),
    ALREADY_USED_ID("이미 사용되고 있는 아이디입니다"),
    NOT_AUTHORIZED("권한이 없습니다"),
    ALREADY_REACTED_FACILITY("이미 좋아요/싫어요를 한 시설입니다"),
    REACT_NOT_EXIST("아직 좋아요/싫어요를 하지 않은 시설입니다."),
    ALREADY_LIKED_REVIEW("이미 좋아요한 리뷰입니다."),
    LIKE_NOT_EXIST("아직 좋아요하지 않은 리뷰입니다"),
    ALREADY_SAVED_FACILITY("이미 저장한 시설입니다"),
    NOT_SAVED_FACILITY("저장한 시설이 아닙니다"),
    CURRENT_PASSWORD_NOT_MATCH("현재 비밀번호가 일치하지 않습니다."),
    TOKEN_EXPIRED("토큰이 만료되었습니다. 다시 로그인해주세요."),
    CANNOT_LIKE_MINE("자신의 리뷰에는 좋아요가 불가능합니다")
    ;
    private final String message;

    private ErrorMessage(String message){
        this.message = message;
    }

}
