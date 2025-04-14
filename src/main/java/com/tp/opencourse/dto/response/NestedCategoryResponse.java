package com.tp.opencourse.dto.response;

import com.tp.opencourse.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NestedCategoryResponse {
    String parentId;
    List<CategoryResponse> categoryResponses;
}
