package com.example.const_team1_backend.facility;

import com.example.const_team1_backend.BaseService;
import com.example.const_team1_backend.common.exception.BadRequestException;
import com.example.const_team1_backend.common.message.ErrorMessage;
import com.example.const_team1_backend.config.s3.S3Service;
import com.example.const_team1_backend.facility.dto.FacilityResponse;
import com.example.const_team1_backend.review.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service("facilityService")
public class FacilityService extends BaseService<Facility,FacilityRepository> {

    @Autowired
    private S3Service s3Service;

    public FacilityService(FacilityRepository repository) {
        super(repository);
    }

    @Transactional
    public Facility findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.FACILITY_NOT_EXIST.getMessage()));
    }

    @Transactional
    public Set<Review> getAllReviewsById(Long id) {
        Facility facility = findById(id);
        return facility.getReviews();
    }

    @Transactional
    public FacilityResponse getFacilityResponseById(Long facilityId) {
        Facility facility = repository.findById(facilityId).orElse(null);
        if (facility == null) {
            throw new BadRequestException("시설이 존재하지 않습니다");
        }
        return FacilityResponse.fromEntity(facility);
    }

    @Transactional
    public Set<FacilityResponse> getFacilityResponsesByFloor(Long floor) {
        List<Facility> facilities = findAll();
        Set<FacilityResponse> responses = new java.util.HashSet<>(Set.of());
        for (Facility facility : facilities) {
            if (facility.getFloor() == floor) {
                responses.add(getFacilityResponseById(facility.getId()));
            }
        }
        return responses;
    }

    @Transactional
    public Set<FacilityResponse> getFacilityResponsesByCategory(Long categoryId) {
        List<Facility> facilities = findAll();
        Set<FacilityResponse> responses = new java.util.HashSet<>(Set.of());
        for (Facility facility : facilities) {
            if (Objects.equals(facility.getCategory().getId(), categoryId)) {
                responses.add(getFacilityResponseById(facility.getId()));
            }
        }
        return responses;
    }
}
