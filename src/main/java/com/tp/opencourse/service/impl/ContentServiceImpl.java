package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.dto.ContentProcessDTO;
import com.tp.opencourse.dto.FileDTO;
import com.tp.opencourse.dto.VideoDTO;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.entity.enums.Type;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.*;
import com.tp.opencourse.repository.*;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.response.SubmitionReponse;
import com.tp.opencourse.service.CertificationService;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.ContentService;
import com.tp.opencourse.service.NotificationService;
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
    private RegisterDetailRepository registerDetailRepository;
    @Autowired
    private SubmitionMapper submitionMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private CertificationService certificationService;

    @Override
    public ContentProcessDTO findById(String userId, String courseId, String id, String contentId) {
        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found user"));
        RegisterDetail registerDetail = user.getRegisters().stream()
                .filter(register -> register.getStatus().equals(RegisterStatus.SUCCESS))
                .flatMap(r -> r.getRegisterDetails().stream())
                .filter(rd -> rd.getCourse().getId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("You are not allowed to access this course"));
        ContentProcess contentProcess = registerDetail.getContentProcesses().stream()
                .filter(process -> process.getId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    Content content = contentRepository.findContentById(contentId)
                            .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));

                    ContentProcess newContentProcess = ContentProcess.builder()
                            .id(id)
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
    public MessageResponse updateContentProcess(String username, String id, Map<String, String> map) {
        ContentProcess contentProcess = contentProcessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found content process"));

        boolean updateStatus = false;
        RegisterDetail rg = contentProcess.getRegisterDetail();
        Course course = contentProcess.getContent().getSection().getCourse();
        int totalLecture = course.getSections().stream()
                .mapToInt(se -> se.getContentList().size())
                .sum();

        // Update watchedTime if present
        String watchedTimeStr = map.get("watchedTime");
        if (watchedTimeStr != null) {
            int watchedTime = Integer.parseInt(watchedTimeStr);
            contentProcess.setWatchedTime(watchedTime);
        }

        // Update status explicitly if provided
        String statusStr = map.get("status");
        if (statusStr != null) {
            boolean newStatus = Boolean.parseBoolean(statusStr);
            boolean currentStatus = contentProcess.isStatus();
            if (newStatus != currentStatus) {
                double delta = newStatus ? (1.0 / totalLecture) : (-1.0 / totalLecture);
                double newPercent = rg.getPercentComplete() + delta;
                rg.setPercentComplete(newPercent);
                contentProcess.setStatus(newStatus);
                if (newStatus)
                    certificationService.createCertification(rg);
                updateStatus = true;
            }
        }


        if (updateStatus) {
            registerDetailRepository.update(rg);
        }

        contentProcessRepository.save(contentProcess);

        return MessageResponse.builder()
                .status(HttpStatus.OK)
                .data(Map.of("sectionId", contentProcess.getContent().getSection().getId(),
                        "contentProcessId", contentProcess.getId(),
                        "status", contentProcess.isStatus()))
                .message("Successfully updated content process")
                .build();
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
    public MessageResponse updateContent(
            String username, String id,
            Map<String, String> field, MultipartFile file) throws IOException {
        Content content = contentRepository.findContentById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption(("Not found content")));
        Optional.ofNullable(content).ifPresent(c -> {
            if (!c.getSection().getCourse().getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });


        if (field.containsKey("name"))
            content.setName(field.get("name"));
        if (file != null) {



            Resource resource = field.get("type").equals("FILE")
                    ? resourceMapper.convertEntity(cloudinaryService.uploadFile(file))
                    : resourceMapper.convertEntity(cloudinaryService.uploadVideo(file));

            Optional.ofNullable(content.getResource()).ifPresent(r -> {
                try {
                    cloudinaryService.removeResource(r.getUrl()
                            , r instanceof Video ? "vide" : "raw");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            content.changeMainResource(resource);
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
    public MessageResponse createSubContent(String username,
                                            Map<String, String> filed, MultipartFile file) throws IOException {
        Content mainContent = contentRepository.findContentById(filed.get("mainContentId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found main content"));

        Optional.ofNullable(mainContent).ifPresent(m -> {
            if (!m.getSection().getCourse().getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });

        Resource resource = resourceMapper.convertEntity(cloudinaryService.uploadFile(file));

        Content content = Content.builder()
                .createdAt(LocalDateTime.now())
                .type(Type.valueOf(filed.get("type")))
                .name(filed.get("name"))
                .mainContent(mainContent)
                .resource(resource)
                .build();
        resource.setContent(content);
        content = contentRepository.updateContent(content);
        return MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully create sub content")
                .data(contentMapper.convertDTO(content))
                .build();
    }



    @Override
    public MessageResponse createContent(String username, Map<String, String> fields
            , MultipartFile file) throws IOException {
        Section section = sectionRepository.findById(fields.get("sectionId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found section"));

        Optional.ofNullable(section).ifPresent(s -> {
            if (!s.getCourse().getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });
        Content content = Content.builder()
                .createdAt(LocalDateTime.now())
                .type(Type.valueOf(fields.get("typeContent")))
                .name(fields.get("name"))
                .section(section)
                .build();
        if (file != null) {
            Resource resource = fields.get("typeResource").equals("FILE")
                    ? resourceMapper.convertEntity(cloudinaryService.uploadFile(file))
                    : resourceMapper.convertEntity(cloudinaryService.uploadVideo(file));
            content.setResource(resource);
            resource.setContent(content);
        }
        content = contentRepository.updateContent(content);

        return MessageResponse.builder()
                .data(contentMapper.convertDTO(content))
                .message("Successfully create content")
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    public void remove(String username, String id) {
        Content content = contentRepository.findContentById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));
        Optional.ofNullable(content).ifPresent(c -> {
            if (!c.getSection().getCourse().getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });

        content.getSection().removeContent(content);
        contentRepository.remove(content);
    }

    @Override
    public MessageResponse removeSubContent(String username, String subContentId) {
        Content content = contentRepository.findContentById(subContentId)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));

        Optional.ofNullable(content).ifPresent(c -> {
            if (!c.getMainContent().getSection().getCourse().getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });
        Optional.ofNullable(content.getResource()).ifPresent(r -> {
            try {
                cloudinaryService.removeResource(r.getUrl()
                        , r instanceof Video ? "vide" : "raw");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        contentRepository.remove(content);
        return MessageResponse.builder()
                .data(null)
                .status(HttpStatus.OK)
                .message("Successfully remove sub contain")
                .build();
    }
}
