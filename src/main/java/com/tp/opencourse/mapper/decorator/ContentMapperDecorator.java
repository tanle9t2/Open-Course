package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.mapper.ContentMapper;
import com.tp.opencourse.mapper.FileMapper;
import com.tp.opencourse.mapper.VideoMapper;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class ContentMapperDecorator implements ContentMapper {

    @Autowired
    private ContentMapper delegate;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private VideoMapper videoMapper;

    @Override
    public ContentDTO convertDTO(Content content) {
        ContentDTO contentDTO = delegate.convertDTO(content);
        List<ContentDTO.SubContent> subContents = content.getSubContents().stream()
                .map(s -> ContentDTO.SubContent.builder()
                        .id(s.getId())
                        .type(s.getType())
                        .file(s.getFile() != null ? fileMapper.convertDTO(s.getFile()) : null)
                        .video(s.getVideo() != null ? videoMapper.convertDTO(s.getVideo()) : null)
                        .name(s.getName())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        contentDTO.setSubContents(subContents);

        return contentDTO;
    }
}
