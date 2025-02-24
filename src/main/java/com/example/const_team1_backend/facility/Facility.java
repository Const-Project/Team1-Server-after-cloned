package com.example.const_team1_backend.facility;

import com.example.const_team1_backend.BaseEntity;
import com.example.const_team1_backend.building.Building;
import com.example.const_team1_backend.category.Category;
import com.example.const_team1_backend.common.utils.OperatingHoursUtils;
import com.example.const_team1_backend.location.Location;
import com.example.const_team1_backend.operatingHours.entity.FacilityOperatingHours;
import com.example.const_team1_backend.operatingHours.entity.OperatingHours;
import com.example.const_team1_backend.reaction.Reaction;
import com.example.const_team1_backend.review.Review;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
public class Facility extends BaseEntity {

    private String name;

    @Embedded
    private Location location = new Location();

    private int floor;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<FacilityOperatingHours> operatingHours;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonManagedReference
    private Set<Reaction> reactions;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonManagedReference
    private Set<Review> reviews;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "building_id")
    @JsonBackReference
    private Building building;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    public Long getTotalLikes() {
        long cnt = 0L;
        for(Reaction reaction : reactions) {
            if(reaction.getType()==1) cnt++;
        }
        return cnt; // 좋아요의 총 개수 반환
    }


    public Long getTotalDislikes() {
        long cnt = 0L;
        for(Reaction reaction : reactions) {
            if(reaction.getType()==0) cnt++;
        }
        return cnt; // 좋아요의 총 개수 반환
    }

    public Long getTotalReviews() {
        return (long) reviews.size(); // 좋아요의 총 개수 반환
    }

    public void addReaction(Reaction reaction) {
        reactions.add(reaction);
        reaction.setFacility(this);
    }

    public void addReviews(Review review) {
        reviews.add(review);
        review.setFacility(this);
    }

    public void deleteReaction(Reaction reaction) {
        reactions.remove(reaction);
    }

    public LocalTime getOpenTime(){
        List<OperatingHours> operatingHoursSet = List.copyOf(operatingHours);
        return OperatingHoursUtils.getOpenTimeForDate(LocalDate.now(),operatingHoursSet);
    }

    public LocalTime getCloseTime(){
        List<OperatingHours> operatingHoursSet = List.copyOf(operatingHours);
        return OperatingHoursUtils.getCloseTimeForDate(LocalDate.now(),operatingHoursSet);
    }
}
