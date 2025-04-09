package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.dto.ContentProcessDTO;
import com.tp.opencourse.dto.FileDTO;
import com.tp.opencourse.dto.VideoDTO;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.entity.enums.Type;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.*;
import com.tp.opencourse.repository.*;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.response.SubmitionReponse;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.ContentService;
import com.tp.opencourse.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private ContentProcessRepository contentProcessRepository;
    @Autowired
    private ContentProcessMapper contentProcessMapper;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubmitionRepository submitionRepository;
    @Autowired
    private SubmitionMapper submitionMapper;
    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public ContentProcessDTO findById(String userId, String courseId, String id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found user"));
        RegisterDetail registerDetail = user.getRegisters().stream()
                .flatMap(r -> r.getRegisterDetails().stream())
                .filter(rd -> rd.getCourse().getId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("You are not allowed to access this course"));

        ContentProcess contentProcess = registerDetail.getContentProcesses().stream()
                .filter(process -> process.getContent().getId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    Content content = contentRepository.findContentById(id)
                            .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));
                    ContentProcess newContentProcess = ContentProcess.builder()
                            .registerDetail(registerDetail)
                            .content(content)
                            .watchedTime(0)
                            .build();
                    registerDetail.addContentProcess(newContentProcess);
                    return contentProcessRepository.save(newContentProcess);
                });

        return contentProcessMapper.convertDTO(contentProcess);
    }

    @Override
    public ContentDTO get(String contentId) {
        Content content = contentRepository.findContentById(contentId)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));

        return contentMapper.convertDTO(content);
    }

    @Override
    public SubmitionReponse findSubmition(String id) {
        Content content = contentRepository.findContentById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));
        List<Submition> submitionList = submitionRepository.findByContent(id);

        return SubmitionReponse.builder()
                .contentDTO(contentMapper.convertDTO(content))
                .submitions(submitionList.stream()
                        .map(submition -> submitionMapper.convertDTO(submition))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public MessageResponse updateContent(String id, Map<String, String> field, MultipartFile file) throws IOException {
        Helper.validateRequiredFields(field, "name");
        Content content = contentRepository.findContentById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption(("Not found content")));

        content.setName(field.get("name"));
        if (file != null) {
            Resource resource = resourceMapper.convertEntity(cloudinaryService.uploadFile(file));
            resource.setContent(content);
            content.setMainContent(content);
        }
        content = contentRepository.updateContent(content);
        return MessageResponse.builder()
                .message("Successfully update content")
                .status(HttpStatus.OK)
                .data(contentMapper.convertDTO(content))
                .build();
    }

    @Override
    public void createExercise(Map<String, String> filed, MultipartFile file) throws IOException {
        Section section = sectionRepository.findById(filed.get("sectionId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found section"));
        Resource resource = resourceMapper.convertEntity(cloudinaryService.uploadFile(file));

        Content content = Content.builder()
                .createdAt(LocalDateTime.now())
                .type(Type.valueOf(filed.get("type")))
                .name(filed.get("name"))
                .resource(resource)
                .build();
        resource.setContent(content);
        section.addContent(content);
        sectionRepository.update(section);
    }

    @Override
    public void createSubContent(Map<String, String> filed, MultipartFile file) throws IOException {
        Content mainContent = contentRepository.findContentById(filed.get("mainContentId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found main content"));
        Resource resource = resourceMapper.convertEntity(cloudinaryService.uploadFile(file));

        Content content = Content.builder()
                .createdAt(LocalDateTime.now())
                .type(Type.valueOf(filed.get("type")))
                .name(filed.get("name"))
                .mainContent(mainContent)
                .resource(resource)
                .build();
        resource.setContent(content);
        mainContent.addSubContent(content);
        contentRepository.updateContent(mainContent);
    }

    @Override
    public void createContent(Map<String, String> filed, MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        // Check if the file is a video based on MIME type
        if (contentType != null && contentType.startsWith("video/")) {
            Section section = sectionRepository.findById(filed.get("sectionId"))
                    .orElseThrow(() -> new ResourceNotFoundExeption("Not found section"));
            VideoDTO videoDTO = cloudinaryService.uploadVideo(file);
            Video video = resourceMapper.convertEntity(videoDTO);

            Content content = Content.builder()
                    .createdAt(LocalDateTime.now())
                    .type(Type.valueOf(filed.get("type")))
                    .name(filed.get("name"))
                    .resource(video)
                    .build();
            video.setContent(content);
            section.addContent(content);
            sectionRepository.update(section);
        } else {
            throw new IllegalArgumentException("Invalid file type. Only video files are allowed.");
        }
    }

    @Override
    public void remove(String id) {
        Content content = contentRepository.findContentById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));

        content.getSection().removeContent(content);
        contentRepository.remove(content);
    }
}
