package com.tp.opencourse.service;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.entity.Content;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ContentService {
    ContentDTO findById(String id);

    void createExercise(Map<String, String> filed, MultipartFile file) throws IOException;

    void remove(String id);
}
