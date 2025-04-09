package com.tp.opencourse.repository;

import java.util.List;

public interface CategoryRepository {
    List<String> getAllCategoryHierachyIds(int left, int right);
}
