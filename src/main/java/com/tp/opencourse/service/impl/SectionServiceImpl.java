package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.repository.SectionRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.SectionService;
import com.tp.opencourse.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class SectionServiceImpl implements SectionService {
    @Autowired
    private SectionRepository sectionRepository;
    @Override
    public SectionDTO findById(String id) {
        return null;
    }

    @Override
    public MessageResponse updateSection(String id, Map<String, String> fields) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found Section"));
        Helper.validateRequiredFields(fields, "name");
        section.setName(fields.get("name"));
        return MessageResponse.builder()
                .status(HttpStatus.OK)
                .data(null)
                .message("Successfully update section")
                .build();
    }

}
