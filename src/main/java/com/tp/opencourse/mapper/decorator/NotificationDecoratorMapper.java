package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.NotificationDTO;
import com.tp.opencourse.entity.Notification;
import com.tp.opencourse.mapper.NotificationMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
public abstract class NotificationDecoratorMapper implements NotificationMapper {
    @Autowired
    private NotificationMapper delegate;

    @Override
    public NotificationDTO convertDTO(Notification notification) {
        NotificationDTO notificationDTO = delegate.convertDTO(notification);

        NotificationDTO.UserInfo userInfo = NotificationDTO.UserInfo.builder()
                .email(notification.getTeacher().getEmail())
                .avt(notification.getTeacher().getAvt())
                .name(notification.getTeacher().getFullName())
                .id(notification.getTeacher().getId())
                .build();

        notificationDTO.setTeacher(userInfo);

        return notificationDTO;
    }
}
