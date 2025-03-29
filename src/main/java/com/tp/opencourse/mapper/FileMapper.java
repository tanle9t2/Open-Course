package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.FileDTO;
import com.tp.opencourse.entity.File;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileDTO convertDTO(File file);

    File convertEntity(FileDTO fileDTO);
}
