package com.example.const_team1_backend.member;

import com.example.const_team1_backend.BaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public interface MemberRepository extends BaseRepository<Member, Long> {
    Optional<Member> findByLoginId(String LoginId);
}
