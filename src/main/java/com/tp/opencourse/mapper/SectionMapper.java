package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.entity.Section;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CourseMapper.class)
public interface SectionMapper {
    SectionDTO convertDTO(Section section);

    Section convertEntity(SectionDTO sectionDTO);
}
