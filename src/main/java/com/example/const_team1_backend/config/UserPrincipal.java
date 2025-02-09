package com.example.const_team1_backend.config;

import com.example.const_team1_backend.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private Long id; // ✅ 사용자 ID
    private String username; // ✅ 로그인 ID (일반적으로 email 또는 userId)
    private String password; // ✅ 암호화된 비밀번호
    private Collection<? extends GrantedAuthority> authorities; // ✅ 권한 목록

    // 계정 상태 필드 (Member 엔티티와 연동)
    private boolean accountNonExpired;
    private boolean accountLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    // Member → UserPrincipal 변환 메서드
    public static UserPrincipal create(Member member) {
        return new UserPrincipal(
                member.getId(),
                member.getLoginId(),
                member.getPassword(),
                authorities(member.getRoles()),
                true, // accountNonExpired
                !member.isAccountLocked(), // accountNonLocked
                true, // credentialsNonExpired
                member.isEnabled() // enabled
        );
    }

    private static Collection<? extends GrantedAuthority> authorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new) // 각 역할 문자열을 권한 객체로 매핑
                .collect(Collectors.toList()); // 리스트로 수집
    }

    // UserDetails 인터페이스 메서드 구현
    @Override
    public String getUsername() {
        return username; // 로그인 ID 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}