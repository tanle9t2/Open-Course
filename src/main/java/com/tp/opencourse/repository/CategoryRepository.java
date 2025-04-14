package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Category;

import java.util.List;

public interface CategoryRepository {
    List<String> getAllCategoryHierachyIds(int left, int right);
    List<Category> getRootCategory();
    List<Category> getCategoryByLevel(String parentId);

}
