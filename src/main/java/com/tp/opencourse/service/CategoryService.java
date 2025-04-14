package com.tp.opencourse.service;

import co.elastic.clients.elasticsearch.license.LicenseStatus;
import com.tp.opencourse.dto.response.CategoryResponse;
import com.tp.opencourse.dto.response.NestedCategoryResponse;
import com.tp.opencourse.entity.Category;

import java.util.List;
import java.util.Locale;

public interface CategoryService {

    List<CategoryResponse> getRootCategory();
    NestedCategoryResponse getNestedCategory(String parentId);
    List<Category> findByParentIdAndLevel(String parentName, Integer level);
}
