package com.tp.opencourse.service;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.dto.ContentProcessDTO;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.response.SubmitionReponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ContentService {
    ContentProcessDTO findById(String userId, String courseId, String id);

    ContentDTO get(String contentId);

    SubmitionReponse findSubmition(String id);

    MessageResponse updateContent(String id, Map<String, String> field, MultipartFile file) throws IOException;

    void createExercise(Map<String, String> filed, MultipartFile file) throws IOException;

    void createSubContent(Map<String, String> filed, MultipartFile file) throws IOException;

    void createContent(Map<String, String> filed, MultipartFile file) throws IOException;

    void remove(String id);

}
