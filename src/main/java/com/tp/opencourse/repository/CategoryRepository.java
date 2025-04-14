package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findFollowLevel(String parentName, int level);
    Optional<Category> findById(String id);
    List<String> getAllCategoryHierachyIds(int left, int right);
    List<Category> getRootCategory();
    List<Category> getCategoryByLevel(String parentId);

}
