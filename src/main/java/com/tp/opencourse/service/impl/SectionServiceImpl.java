package com.tp.opencourse.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.dto.event.NotificationEvent;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.NotificationMapper;
import com.tp.opencourse.mapper.SectionMapper;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.NotificationRepository;
import com.tp.opencourse.repository.SectionRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.SectionService;
import com.tp.opencourse.service.kafka.NotificationProducer;
import com.tp.opencourse.utils.Helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Service
@Transactional
public class SectionServiceImpl implements SectionService {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SectionMapper sectionMapper;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationProducer notificationProducer;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public SectionDTO findById(String id) {
        return null;
    }
    @Override
    public List<SectionDTO> findByCourseId(String courseId) {
        List<Section> sections = sectionRepository.findByCourseId(courseId);
        return sections.stream()
                .map(sectionMapper::convertDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    @Override
    public MessageResponse updateSection(String username, String id, Map<String, String> fields) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Section"));
        Optional.ofNullable(section).ifPresent(s -> {
            if (!section.getCourse().getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });


        Helper.validateRequiredFields(fields, "name");
        section.setName(fields.get("name"));
        return MessageResponse.builder()
                .status(HttpStatus.OK)
                .data(null)
                .message("Successfully update section")
                .build();
    }

    @Override
    public MessageResponse createSection(String username, Map<String, String> fields) throws JsonProcessingException {
        Helper.validateRequiredFields(fields, "name", "courseId");
        Course course = courseRepository.findById(fields.get("courseId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));
        User teacher;
        Optional.ofNullable(course).ifPresent(c -> {
            if (!c.getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });

        Section section = Section.builder()
                .name(fields.get("name"))
                .course(course)
                .createdAt(LocalDateTime.now())
                .build();
        section = sectionRepository.create(section);

        //
        Map<String, String> content = Map.of("content", String.format("New lesson: %s", section.getName()),
                "courseId", course.getId(),
                "courseUrl", "k",
                "courseBanner", course.getBanner(),
                "courseName", course.getName());

        Notification notification = Notification.builder()
                .teacher(course.getTeacher())
                .createdAt(LocalDateTime.now())
                .content(objectMapper.valueToTree(content))
                .build();
        notification = notificationRepository.create(notification);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .eventId(UUID.randomUUID())
                .eventDate(new Date())
                .notification(notificationMapper.convertDTO(notification))
                .build();
        notificationProducer.sendMessage(notificationEvent);

        return MessageResponse.builder()
                .message("Successfully create section")
                .status(HttpStatus.OK)
                .data(sectionMapper.convertDTO(section))
                .build();
    }

    private void removeResource(Resource resource) throws IOException {
        String type = (resource instanceof Video) ? "video" : "raw";
        cloudinaryService.removeResource(resource.getUrl(), type);
    }

    @Override
    public void deleteSection(String username, String id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found section"));
        Optional.ofNullable(section).ifPresent(s -> {
            if (!section.getCourse().getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });

        sectionRepository.removeSection(section);
        if (section.getContentList() != null) {
            for (Content content : section.getContentList()) {
                try {
                    removeResource(content.getResource());

                    for (Content sub : content.getSubContents()) {
                        removeResource(sub.getResource());
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to remove resources from Cloudinary", e);
                }
            }
        }
    }

}
