package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.mapper.ContentMapper;
import com.tp.opencourse.mapper.ResourceMapper;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
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
        if (content.getSubContents() != null) {
            List<ContentDTO.SubContent> subContents = content.getSubContents().stream()
                    .map(s -> ContentDTO.SubContent.builder()
                            .id(s.getId())
                            .type(s.getType())
                            .name(s.getName())
                            .createdAt(s.getCreatedAt())
                            .resource(resourceMapper.convertDTO(s.getResource()))
                            .build())
                    .sorted(Comparator.comparing(ContentDTO.SubContent::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            contentDTO.setSubContents(subContents);
        }
        return contentDTO;
    }
}
