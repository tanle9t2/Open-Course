package com.tp.opencourse.service;

import co.elastic.clients.elasticsearch.license.LicenseStatus;
import com.tp.opencourse.dto.response.CategoryResponse;
import com.tp.opencourse.dto.response.NestedCategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getRootCategory();
    NestedCategoryResponse getNestedCategory(String parentId);
}
