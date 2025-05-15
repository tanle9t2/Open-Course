package com.tp.opencourse.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.opencourse.dto.event.NotificationEvent;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Notification;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.NotificationMapper;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.NotificationRepository;
import com.tp.opencourse.service.NotificationService;
import com.tp.opencourse.service.kafka.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationProducer notificationProducer;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void createNotification(User user, Map<String, String> data) throws JsonProcessingException {
        Notification notification = Notification.builder()
                .teacher(user)
                .createdAt(LocalDateTime.now())
                .content(objectMapper.valueToTree(data))
                .build();
        notification = notificationRepository.save(notification);
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .eventId(UUID.randomUUID())
                .eventDate(new Date())
                .notification(notificationMapper.convertDTO(notification))
                .build();

        notificationProducer.sendMessage(notificationEvent);
    }

    @Override
    @Transactional
    public void createNotificationInCourse(String username, String courseId, String content) throws JsonProcessingException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));
        if (!course.getTeacher().getUsername().equals(username))
            throw new AccessDeniedException("You don't have permission for this course");
        Map<String, String> data = Map.of("content", content,
                "courseId", course.getId(),
                "courseUrl", "k",
                "courseBanner", course.getBanner(),
                "courseName", course.getName());


        Notification notification = Notification.builder()
                .teacher(course.getTeacher())
                .createdAt(LocalDateTime.now())
                .content(objectMapper.valueToTree(data))
                .build();
        notification = notificationRepository.save(notification);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .eventId(UUID.randomUUID())
                .eventDate(new Date())
                .notification(notificationMapper.convertDTO(notification))
                .build();
        notificationProducer.sendMessage(notificationEvent);
    }
}
