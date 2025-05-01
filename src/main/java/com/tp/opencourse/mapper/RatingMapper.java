package com.tp.opencourse.mapper;


import com.tp.opencourse.dto.response.RatingResponse;
import com.tp.opencourse.entity.Rating;
import com.tp.opencourse.mapper.decorator.RatingMapperDecorator;
import com.tp.opencourse.mapper.decorator.ResourceDecoratorMapper;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(RatingMapperDecorator.class)
public interface RatingMapper {
    RatingResponse convertEntityToResponse(Rating rating);
}
