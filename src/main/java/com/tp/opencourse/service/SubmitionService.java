package com.tp.opencourse.service;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.response.MessageResponse;

public interface SubmitionService {

    MessageResponse createComment(String id, CommentDTO commentDTO);

    SubmitionDTO findById(String id);

    PageResponse<SubmitionDTO> findSubmissionsByCourseId(String courseId, int page, int size
            , String sortField, String order);

    void updateMark(String id, double mark);

    void deleteComment(String commentId);
}
