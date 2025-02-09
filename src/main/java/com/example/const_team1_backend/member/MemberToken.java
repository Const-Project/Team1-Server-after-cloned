package com.example.const_team1_backend.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "member_token",indexes = {
        @Index(name = "idx_access_token", columnList = "accessToken")
})
public class MemberToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // ✅ 리프레시 토큰 생성 시간

    // [변경 1] 1:N 관계로 수정 (기존 1:1 → 1:N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column

    private String accessToken;

    @Column
    private String refreshToken;

    private int renewCount;
    private LocalDateTime lastLoginTime;

    // [변경 2] Optimistic Lock을 위한 버전 관리 추가
    @Version
    private Long version;

    // [추가] 생성 시점에 자동 설정
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}