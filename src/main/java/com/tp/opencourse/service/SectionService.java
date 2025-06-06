package com.tp.opencourse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.dto.response.SectionSummaryResponse;
import com.tp.opencourse.response.MessageResponse;

import java.util.List;
import java.util.Map;

public interface SectionService {
    SectionDTO findById(String id);
    SectionSummaryResponse findByCourseId(String courseId);

    MessageResponse updateSection(String username,String id, Map<String, String> fields);

    MessageResponse createSection(String username,Map<String, String> fields) throws JsonProcessingException;

    void deleteSection(String username,String id);
}
