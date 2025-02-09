package com.example.const_team1_backend.reaction;

import com.example.const_team1_backend.BaseService;
import com.example.const_team1_backend.review.Review;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service("reactionService")
public class ReactionService extends BaseService<Reaction, ReactionRepository> {
    public ReactionService(ReactionRepository repository) {
        super(repository);
    }

    public Optional<Reaction> findByFacilityIdAndMemberId(Long facilityId, Long memberId) {
        return repository.findByFacilityIdAndMemberId(facilityId, memberId);
    }

    public void createReaction(Reaction reaction) {
        repository.save(reaction);
    }
}
