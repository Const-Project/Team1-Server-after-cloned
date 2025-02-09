package com.example.const_team1_backend.reaction.dto;

import com.example.const_team1_backend.reaction.Reaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponse {
    private Long reactionId;
    private int type;

    public static ReactionResponse fromEntity(Reaction reaction) {
        return new ReactionResponse(
                reaction.getId(),
                reaction.getType()
        );
    }
}
