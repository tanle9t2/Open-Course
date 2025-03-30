package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.entity.Comment;
import com.tp.opencourse.entity.Submition;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.CommentMapper;
import com.tp.opencourse.mapper.SubmitionMapper;
import com.tp.opencourse.repository.CommentRepository;
import com.tp.opencourse.repository.SubmitionRepository;
import com.tp.opencourse.service.SubmitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class SubmitionServiceImpl implements SubmitionService {
    @Autowired
    private SubmitionRepository submitionRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SubmitionMapper submitionMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public void createComment(String id, CommentDTO commentDTO) {
        Submition submition = submitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found submition"));
        Comment comment = commentMapper.convertEntity(commentDTO);
        comment.setCreatedAt(LocalDateTime.now());
        submition.addComment(comment);

        submitionRepository.update(submition);
    }

    @Override
    public SubmitionDTO findById(String id) {
        Submition submition = submitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Submition"));
        return submitionMapper.convertDTO(submition);
    }

    @Override
    public void updateMark(String id, double mark) {
        Submition submition = submitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Submition"));
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
