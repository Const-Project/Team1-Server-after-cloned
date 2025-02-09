package com.example.const_team1_backend.review;

import com.example.const_team1_backend.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends BaseRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.id = :id AND r.member.id = :memberId")
    Optional<Review> findByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);
}
