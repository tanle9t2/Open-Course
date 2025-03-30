package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.ContentProcessDTO;
import com.tp.opencourse.entity.ContentProcess;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ContentMapper.class})
public interface ContentProcessMapper {
    ContentProcessDTO convertDTO(ContentProcess contentProcess);

    ContentProcess convertEntity(ContentProcessDTO contentProcessDTO);
}
