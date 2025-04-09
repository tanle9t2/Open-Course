package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.mapper.decorator.ContentMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ResourceMapper.class, SectionMapper.class})
@DecoratedWith(ContentMapperDecorator.class)
public interface ContentMapper {
    ContentDTO convertDTO(Content content);

    Content convertEntity(ContentDTO contentDTO);
}
