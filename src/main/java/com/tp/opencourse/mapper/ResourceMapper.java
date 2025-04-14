package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.FileDTO;
import com.tp.opencourse.dto.ResourceDTO;
import com.tp.opencourse.dto.VideoDTO;
import com.tp.opencourse.entity.File;
import com.tp.opencourse.entity.Resource;
import com.tp.opencourse.entity.Video;
import com.tp.opencourse.mapper.decorator.ResourceDecoratorMapper;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(ResourceDecoratorMapper.class)
public interface ResourceMapper {
    @Mapping(target = "type", constant = "FILE")
    FileDTO convertDTO(File file);

    @Mapping(target = "type", constant = "VIDEO")
    @Mapping(source = "duration", target = "duration")
    VideoDTO convertDTO(Video video);


    File convertEntity(FileDTO file);


    Video convertEntity(VideoDTO video);

    default ResourceDTO convertDTO(Resource resource) {
        if (resource == null) return null;
        if (resource instanceof File) {
            return convertDTO((File) resource);
        } else if (resource instanceof Video) {
            return convertDTO((Video) resource);
        } else {
            throw new IllegalArgumentException("Unknown subclass of Resource: " + resource.getClass());
        }
    }

    default Resource convertEntity(ResourceDTO resource) {
        if (resource == null) return null;
        if (resource instanceof FileDTO) {
            return convertEntity((FileDTO) resource);
        } else if (resource instanceof VideoDTO) {
            return convertEntity((VideoDTO) resource);
        } else {
            throw new IllegalArgumentException("Unknown subclass of Resource: " + resource.getClass());
        }
    }

}
