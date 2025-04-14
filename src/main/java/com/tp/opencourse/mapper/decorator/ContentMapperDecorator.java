package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.mapper.ContentMapper;
import com.tp.opencourse.mapper.ResourceMapper;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class ContentMapperDecorator implements ContentMapper {

    @Autowired
    private ContentMapper delegate;
    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public ContentDTO convertDTO(Content content) {
        ContentDTO contentDTO = delegate.convertDTO(content);
        List<ContentDTO.SubContent> subContents = Optional.ofNullable(content.getSubContents())
                .orElse(Collections.emptyList())
                .stream()
                .map(s -> ContentDTO.SubContent.builder()
                        .id(s.getId())
                        .type(s.getType())
                        .name(s.getName())
                        .createdAt(s.getCreatedAt())
                        .resource(resourceMapper.convertDTO(s.getResource()))
                        .build())
                .sorted(Comparator.comparing(ContentDTO.SubContent::getCreatedAt))
                .collect(Collectors.toList());
        contentDTO.setSubContents(subContents);
        return contentDTO;
    }

    @Override
    public Content convertEntity(ContentDTO contentDTO) {
        return delegate.convertEntity(contentDTO);
    }
}
