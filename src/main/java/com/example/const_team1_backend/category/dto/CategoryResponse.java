package com.example.const_team1_backend.category.dto;

import com.example.const_team1_backend.category.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long categoryId;

    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getId()
        );
    }
}
