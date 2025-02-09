package com.example.const_team1_backend.review_like;

import com.example.const_team1_backend.BaseEntity;
import com.example.const_team1_backend.member.Member;
import com.example.const_team1_backend.review.Review;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class ReviewLike extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "review_id")
    @JsonBackReference
    private Review review;

}
