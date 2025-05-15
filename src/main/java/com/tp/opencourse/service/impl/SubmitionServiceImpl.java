package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.dto.request.SubmissionRequest;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.CommentMapper;
import com.tp.opencourse.mapper.SubmitionMapper;
import com.tp.opencourse.repository.*;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.SubmitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubmitionServiceImpl implements SubmitionService {
    @Autowired
    private SubmitionRepository submitionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SubmitionMapper submitionMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ContentProcessRepository contentRepository;

    @Override
    public MessageResponse createComment(String username, String id, CommentDTO commentDTO) {
        Submition submition = submitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found submition"));

        User user = userRepository.findByUsername(username).get();
        Comment comment = commentMapper.convertEntity(commentDTO);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);


        submition.addComment(comment);

        submitionRepository.save(submition);
        return MessageResponse.builder()
                .message("Successfully create comment")
                .status(HttpStatus.OK)
                .data(commentMapper.convertDTO(comment))
                .build();
    }

    @Override
    public MessageResponse createSubmission(String username, SubmissionRequest request) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found username"));

        ContentProcess content = contentRepository.findById(request.getContentId())
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found content"));

        Submition submition = Submition.builder()
                .createdAt(LocalDateTime.now())
                .answer(request.getAnswer())
                .student(student)
                .content(content)
                .build();

        content.setStatus(true);

        submitionRepository.save(submition);
        contentRepository.save(content);
        return MessageResponse.builder()
                .message("Successfully create submission")
                .status(HttpStatus.CREATED)
                .data(submitionMapper.convertDTO(submition))
                .build();
    }

    @Override
    public SubmitionDTO findById(String id) {
        Submition submition = submitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Submition"));
        return submitionMapper.convertDTO(submition);
    }

    @Override
    public SubmitionDTO findByContentProcessId(String username, String contentProcessId) {
        Submition submition = submitionRepository.findByContentProcess(contentProcessId, username)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Submission"));

        return submitionMapper.convertDTO(submition);
    }

    @Override
    public PageResponseT<SubmitionDTO> findSubmissionsByCourseId(String username, String courseId, int page,
                                                                 int size, String sortField, String order) {
        Page<Submition> submitionPage = submitionRepository.findByCourseId(username, courseId, page, size, sortField, order);
        return PageResponseT.<SubmitionDTO>builder()
                .totalPages(submitionPage.getTotalPages())
                .status(HttpStatus.OK)
                .data(submitionPage.getContent().stream()
                        .map(s -> submitionMapper.convertDTO(s))
                        .collect(Collectors.toList()))
                .page(submitionPage.getPageNumber())
                .count((long) submitionPage.getContent().size())
                .build();
    }

    @Override
    public void updateMark(String username, String id, double mark) {
        Submition submition = submitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Submission"));
        Optional.ofNullable(submition).ifPresent(s -> {
            if (!s.getContent().getContent().getSection().getCourse().getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });


        submition.setMark(mark);
        submitionRepository.save(submition);
    }

    @Override
    public void deleteComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found comment"));
        commentRepository.delete(comment);
    }

}
