package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.VideoDTO;
import com.tp.opencourse.entity.Video;
import com.tp.opencourse.mapper.ResourceMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
public abstract class ResourceDecoratorMapper implements ResourceMapper {
    @Autowired
    private ResourceMapper delegate;

    @Override
    public Video convertEntity(VideoDTO videoDTO) {
        Video video = delegate.convertEntity(videoDTO);
        video.setName(videoDTO.getName());
        video.setDuration(videoDTO.getDuration());
        video.setUrl(videoDTO.getUrl());
        video.setCreatedAt(videoDTO.getCreatedAt());

        return video;
    }
}
