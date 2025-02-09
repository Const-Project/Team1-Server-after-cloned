package com.example.const_team1_backend.member;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {
    Optional<MemberToken> findByRefreshToken(String token);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT mt FROM MemberToken mt WHERE mt.member = :member")
    List<MemberToken> findByMember(@Param("member") Member member);

    long countByMemberAndCreatedAtAfter(Member member, LocalDateTime dateTime);

    void deleteByMember(Member member);

    Optional<MemberToken> findByAccessToken(String accessToken);
}
