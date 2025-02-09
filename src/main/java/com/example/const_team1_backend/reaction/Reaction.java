package com.example.const_team1_backend.reaction;

import com.example.const_team1_backend.BaseEntity;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.member.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reaction extends BaseEntity {

    public Reaction(int type) {
        this.type = type;
    }

    private int type; // e.g., "like", "dislike"

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    @JsonBackReference
    private Facility facility;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;
}
