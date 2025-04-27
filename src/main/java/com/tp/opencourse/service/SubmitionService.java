package com.tp.opencourse.service;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.response.MessageResponse;

public interface SubmitionService {

    MessageResponse createComment(String username, String id, CommentDTO commentDTO);

    SubmitionDTO findById(String id);

    PageResponseT<SubmitionDTO> findSubmissionsByCourseId(String username, String courseId, int page, int size
            , String sortField, String order);

    void updateMark(String username, String id, double mark);

    void deleteComment(String commentId);
}
