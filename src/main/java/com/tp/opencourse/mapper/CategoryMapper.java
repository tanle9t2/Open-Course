package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.response.CategoryResponse;
import com.tp.opencourse.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse convertResponse(Category category);
}