package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.VideoDTO;
import com.tp.opencourse.entity.Video;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    VideoDTO convertDTO(Video video);

    Video convertEntity(VideoDTO videoDTO);
}
