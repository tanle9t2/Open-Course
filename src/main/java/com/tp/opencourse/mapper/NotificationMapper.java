package com.tp.opencourse.mapper;

import com.thoughtworks.qdox.model.expression.Not;
import com.tp.opencourse.dto.NotificationDTO;
import com.tp.opencourse.entity.Notification;
import com.tp.opencourse.mapper.decorator.NotificationDecoratorMapper;
import com.tp.opencourse.mapper.decorator.ResourceDecoratorMapper;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(NotificationDecoratorMapper.class)
public interface NotificationMapper {
    NotificationDTO convertDTO(Notification notification);
}
