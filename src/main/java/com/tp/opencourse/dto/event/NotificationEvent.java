package com.tp.opencourse.dto.event;

import com.tp.opencourse.dto.NotificationDTO;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Notification;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.entity.UserNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements Event {
    private UUID eventId = UUID.randomUUID();
    private Date eventDate = new Date();
    private NotificationDTO notification;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseInfo {
        private String id;
        private String name;
        private String banner;
        private String courseUrl;
    }

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public Date getDate() {
        return this.eventDate;
    }
}
