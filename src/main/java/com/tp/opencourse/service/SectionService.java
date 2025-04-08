package com.tp.opencourse.service;

import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.response.MessageResponse;

import java.util.Map;

public interface SectionService {
    SectionDTO findById(String id);

    MessageResponse updateSection(String id, Map<String,String> fields);
}
