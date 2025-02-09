package com.example.const_team1_backend.category;

import com.example.const_team1_backend.BaseController;
import com.example.const_team1_backend.category.dto.CategoryResponse;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.facility.FacilityService;
import com.example.const_team1_backend.facility.dto.FacilityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RequestMapping(value = "/v1/category",produces = "application/json; charset=UTF-8")
@RestController
public class CategoryController extends BaseController<Category,CategoryService> {

    @Autowired
    private FacilityService facilityService;

    public CategoryController(CategoryService service) {
        super(service);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = service.findAll();
        List<CategoryResponse> responses = categories.stream()
                .map(CategoryResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        Category category = service.findById(categoryId);
        return ResponseEntity.ok(CategoryResponse.fromEntity(category));
    }

    @GetMapping("/facilities/{category_id}")
    public ResponseEntity<Set<FacilityResponse>> getFacilities(@PathVariable Long category_id) {
        Set<Facility> facilities = service.getCategoryFacilitiesById(category_id);
        Set<FacilityResponse> responses = new HashSet<>();
        for(Facility facility : facilities) {
            responses.add(facilityService.getFacilityResponseById(facility.getId()));
        }
        return ResponseEntity.ok(responses);
    }
}
