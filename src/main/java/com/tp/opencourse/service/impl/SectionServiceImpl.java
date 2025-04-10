package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.SectionMapper;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.SectionRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.SectionService;
import com.tp.opencourse.utils.Helper;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

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

    @Override
    public SectionDTO findById(String id) {
        return null;
    }

    @Override
    public MessageResponse updateSection(String id, Map<String, String> fields) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Section"));
        Helper.validateRequiredFields(fields, "name");
        section.setName(fields.get("name"));
        return MessageResponse.builder()
                .status(HttpStatus.OK)
                .data(null)
                .message("Successfully update section")
                .build();
    }

    @Override
    public MessageResponse createSection(Map<String, String> fields) {
        Helper.validateRequiredFields(fields, "name", "courseId");
        Course course = courseRepository.findById(fields.get("courseId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));
        Section section = Section.builder()
                .name(fields.get("name"))
                .course(course)
                .createdAt(LocalDateTime.now())
                .build();

        section = sectionRepository.create(section);

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
    public void deleteSection(String id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found section"));
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
