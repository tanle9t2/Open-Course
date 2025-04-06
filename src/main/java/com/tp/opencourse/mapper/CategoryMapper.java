package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.CategoryDTO;
import com.tp.opencourse.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO convertDTO(Category category);

    Category convertEntity(CategoryDTO categoryDTO);
}
