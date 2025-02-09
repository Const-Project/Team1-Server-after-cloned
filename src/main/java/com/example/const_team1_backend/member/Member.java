package com.example.const_team1_backend.member;

import com.example.const_team1_backend.BaseEntity;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.reaction.Reaction;
import com.example.const_team1_backend.review.Review;
import com.example.const_team1_backend.review_like.ReviewLike;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Member extends BaseEntity {

    @Column(name = "is_locked",nullable = false)
    private Boolean accountLocked = false;

    @Column(name = "enabled",nullable = false)
    private Boolean enabled = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<String> roles; // 권한 리스트

    private String username;

    @Column(name = "login_id") // 추가해야 하는 부분
    @JsonIgnore
    private String loginId;

    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<MemberToken> tokens;

    @Version
    private Long version;

    private LocalDateTime lastLogoutAt;

    private String profileImageUrl;

    private String introduction;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonManagedReference
    private Set<Reaction> reactions;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonManagedReference
    private Set<Review> reviews;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "member_saved_facilities",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    @JsonManagedReference
    private Set<Facility> savedFacilities;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonManagedReference
    private Set<ReviewLike> userReviewLikes;

    // Getters and setters

    public Long getTotalSavedFacilities() {
        return (long) savedFacilities.size();
    }

    public void addReaction(Reaction reaction) {
        reactions.add(reaction);
        reaction.setMember(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setMember(this);
    }

    public Boolean isAccountLocked(){
        return accountLocked;
    }

    public Boolean isEnabled(){
        return enabled;
    }


    public void deleteReaction(Reaction reaction) {
        reactions.remove(reaction);
    }


    public void saveFacility(Facility facility) {
        savedFacilities.add(facility);
    }

    public void deleteFacility(Facility facility) {
        savedFacilities.remove(facility);
    }

    public void addReviewLike(ReviewLike reviewLike) {
        userReviewLikes.add(reviewLike);
        reviewLike.setMember(this);
    }

    public void deleteReviewLike(ReviewLike reviewLike) {
        userReviewLikes.remove(reviewLike);
    }

}