package com.example.const_team1_backend.review;

import com.example.const_team1_backend.BaseEntity;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.member.Member;
import com.example.const_team1_backend.review_like.ReviewLike;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Review extends BaseEntity {

    private String content;

    public Review(String content) {
        this.content = content;
        this.reviewLikes = new HashSet<>();
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    @JsonBackReference
    private Facility facility;

    @ManyToMany
    @JoinTable(name = "review_liked_members",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    @JsonBackReference
    private Set<Member> likedMembers;

    // Getters and setters

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonManagedReference
    private Set<ReviewLike> reviewLikes;

    public Long getTotalLikes(){
        return reviewLikes != null ? (long) reviewLikes.size() : 0;
    }

    public void addLike(ReviewLike reviewLike) {
        reviewLikes.add(reviewLike);
        reviewLike.setReview(this);
    }

    public void removeLike(ReviewLike reviewLike) {
        reviewLikes.remove(reviewLike);
    }
}
