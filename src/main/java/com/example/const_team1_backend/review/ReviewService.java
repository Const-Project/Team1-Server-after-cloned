package com.example.const_team1_backend.review;

import com.example.const_team1_backend.BaseService;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service("reviewService")
public class ReviewService extends BaseService<Review,ReviewRepository> {
    public ReviewService(ReviewRepository repository) {
        super(repository);
    }

    public Review findByIdAndMemberIdOrThrow(Long id, Long memberId) throws AccessDeniedException {
        return repository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new AccessDeniedException("You do not have permission to delete this review"));
    }

}
