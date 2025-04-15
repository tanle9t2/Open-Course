package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.entity.Comment;
import com.tp.opencourse.entity.Submition;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.CommentMapper;
import com.tp.opencourse.mapper.SubmitionMapper;
import com.tp.opencourse.repository.CommentRepository;
import com.tp.opencourse.repository.SubmitionRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.SubmitionService;
import com.tp.opencourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Override
    public MessageResponse createComment(String username, String id, CommentDTO commentDTO) {
        Submition submition = submitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found submition"));

        User user = userRepository.findByUsername(username).get();
        Comment comment = commentMapper.convertEntity(commentDTO);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);


        submition.addComment(comment);

        submitionRepository.update(submition);
        return MessageResponse.builder()
                .message("Successfully create comment")
                .status(HttpStatus.OK)
                .data(commentMapper.convertDTO(comment))
                .build();
    }

    @Override
    public SubmitionDTO findById(String id) {
        Submition submition = submitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Submition"));
        return submitionMapper.convertDTO(submition);
    }

    @Override
    public PageResponse<SubmitionDTO> findSubmissionsByCourseId(String username, String courseId, int page,
                                                                int size, String sortField, String order) {
        Page<Submition> submitionPage = submitionRepository.findByCourseId(username, courseId, page, size, sortField, order);
        return PageResponse.<SubmitionDTO>builder()
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
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Submition"));
        Optional.ofNullable(submition).ifPresent(s -> {
            if (!s.getContent().getSection().getCourse().getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });


        submition.setMark(mark);
        submitionRepository.update(submition);
    }

    @Override
    public void deleteComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found comment"));
        commentRepository.delete(comment);
    }

}
