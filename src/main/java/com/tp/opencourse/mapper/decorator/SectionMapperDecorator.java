package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.mapper.SectionMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class SectionMapperDecorator implements SectionMapper {
    @Autowired
    private SectionMapper delegate;

    @Override
    public SectionDTO convertDTO(Section section) {
        SectionDTO sectionDTO = delegate.convertDTO(section);
        if (sectionDTO.getContentList() != null)
            sectionDTO.getContentList().sort(Comparator.comparing(ContentDTO::getCreatedAt));

        return sectionDTO;
    }
}
