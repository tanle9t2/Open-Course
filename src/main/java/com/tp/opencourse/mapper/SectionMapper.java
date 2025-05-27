package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.dto.response.SectionSummaryResponse;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.mapper.decorator.CourseMapperDecorator;
import com.tp.opencourse.mapper.decorator.SectionMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ContentMapper.class)
@DecoratedWith(SectionMapperDecorator.class)
public interface SectionMapper {
    SectionDTO convertDTO(Section section);

    Section convertEntity(SectionDTO sectionDTO);

    SectionSummaryResponse.SectionResponse convertResponse(Section section);

}
