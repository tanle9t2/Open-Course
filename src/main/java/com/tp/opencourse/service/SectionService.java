package com.tp.opencourse.service;

import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.response.MessageResponse;

import java.util.List;
import java.util.Map;

public interface SectionService {
    SectionDTO findById(String id);
    List<SectionDTO> findByCourseId(String courseId);

    MessageResponse updateSection(String id, Map<String, String> fields);

    MessageResponse createSection(Map<String, String> fields);

    void deleteSection(String id);
}
