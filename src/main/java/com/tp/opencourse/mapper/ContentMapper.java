package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.entity.Content;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {FileMapper.class, SectionMapper.class})
public interface ContentMapper {
    ContentDTO convertDTO(Content content);

    Content convertEntity(ContentDTO contentDTO);
}
