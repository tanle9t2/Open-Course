package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.NotificationDTO;
import com.tp.opencourse.dto.UserNotificationDTO;
import com.tp.opencourse.entity.Notification;
import com.tp.opencourse.entity.UserNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {NotificationMapper.class})
public interface UserNotificationMapper {

    @Mapping(source = "read",target = "isRead")
    UserNotificationDTO convertDTO(UserNotification userNotification);
}
