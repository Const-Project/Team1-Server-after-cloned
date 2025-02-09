package com.example.const_team1_backend.category;

import com.example.const_team1_backend.BaseService;
import com.example.const_team1_backend.building.Building;
import com.example.const_team1_backend.facility.Facility;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("categoryService")
public class CategoryService extends BaseService<Category,CategoryRepository> {
    public CategoryService(CategoryRepository repository) {
        super(repository);
    }

    public Set<Facility> getCategoryFacilitiesById(Long id) {
        Category category = findById(id);
        return category.getFacilities();
    }

}
