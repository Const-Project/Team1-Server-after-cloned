package com.example.const_team1_backend.config.service;

import com.example.const_team1_backend.config.UserPrincipal;
import com.example.const_team1_backend.member.Member;
import com.example.const_team1_backend.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isAccountLocked = member.getAccountLocked() != null ? member.getAccountLocked() : false;
        boolean isEnabled = member.getEnabled() != null ? member.getEnabled() : true;

        // 계정 상태 체크 추가
        if (isAccountLocked) {
            throw new LockedException("계정이 잠겼습니다.");
        }
        if (!isEnabled) {
            throw new DisabledException("계정이 비활성화되었습니다.");
        }

        return UserPrincipal.create(member); // ✅ UserPrincipal 반환
    }
}
