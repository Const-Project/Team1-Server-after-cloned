package com.example.const_team1_backend.reaction;

import com.example.const_team1_backend.BaseRepository;
import com.example.const_team1_backend.review.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends BaseRepository<Reaction, Long> {
    @Query("SELECT r FROM Reaction r WHERE r.facility.id = :facilityId AND r.member.id = :memberId")
    Optional<Reaction> findByFacilityIdAndMemberId(@Param("facilityId") Long facilityId, @Param("memberId") Long memberId);
}
