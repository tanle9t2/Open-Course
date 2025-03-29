package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.dto.FileDTO;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.entity.File;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.entity.enums.Type;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.ContentMapper;
import com.tp.opencourse.mapper.FileMapper;
import com.tp.opencourse.repository.ContentRepository;
import com.tp.opencourse.repository.SectionRepository;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private FileMapper fileMapper;

    @Override
    public ContentDTO findById(String id) {
        Content content = contentRepository.findContentById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));
        return contentMapper.convertDTO(content);
    }

    @Override
    public void createExercise(Map<String, String> filed, MultipartFile file) throws IOException {
        Section section = sectionRepository.findById(filed.get("sectionId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found section"));
        File resource = fileMapper.convertEntity(cloudinaryService.uploadFile(file));

        Content content = Content.builder()
                .createdAt(LocalDateTime.now())
                .type(Type.valueOf(filed.get("type")))
                .name(filed.get("name"))
                .file(resource)
                .build();

        resource.setContent(content);
        section.addContent(content);

        sectionRepository.update(section);
    }

    @Override
    public void remove(String id) {
        Content content = contentRepository.findContentById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));

        content.getSection().removeContent(content);
        contentRepository.remove(content);
    }
}
