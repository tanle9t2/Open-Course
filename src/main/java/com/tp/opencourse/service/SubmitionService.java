package com.tp.opencourse.service;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.SubmitionDTO;

public interface SubmitionService {

    void createComment(String id,CommentDTO commentDTO);
    SubmitionDTO findById(String id);

    void updateMark(String id, double mark);

    void deleteComment( String commentId);
}
