package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.NotificationDTO;
import com.tp.opencourse.dto.UserNotificationDTO;
import com.tp.opencourse.entity.Notification;
import com.tp.opencourse.entity.UserNotification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {NotificationMapper.class})
public interface UserNotificationMapper {

    UserNotificationDTO convertDTO(UserNotification userNotification);
}
