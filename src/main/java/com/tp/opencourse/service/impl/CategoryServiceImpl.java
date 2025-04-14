package com.tp.opencourse.service.impl;


import com.tp.opencourse.dto.response.CategoryResponse;
import com.tp.opencourse.dto.response.NestedCategoryResponse;
import com.tp.opencourse.entity.Category;
import com.tp.opencourse.mapper.CategoryMapper;
import com.tp.opencourse.repository.CategoryRepository;
import com.tp.opencourse.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getRootCategory() {
        List<Category> cate = categoryRepository.getRootCategory();
        return cate.stream().map(categoryMapper::convertResponse).collect(Collectors.toList());
    }

    @Override
    public NestedCategoryResponse getNestedCategory(String parentId) {
        List<Category> categories = categoryRepository.getCategoryByLevel(parentId);
        return NestedCategoryResponse
                .builder()
                .parentId(parentId)
                .categoryResponses(categories.stream().map(categoryMapper::convertResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
