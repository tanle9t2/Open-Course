package com.tp.opencourse.mapper;


import com.tp.opencourse.dto.response.LearningResponse;
import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.mapper.decorator.CourseMapperDecorator;
import com.tp.opencourse.mapper.decorator.RegisterMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
@DecoratedWith(RegisterMapperDecorator.class)
public interface RegisterMapper {
    List<LearningResponse> convertEntityToLearningResponse(List<RegisterDetail> registerDetails);
}
