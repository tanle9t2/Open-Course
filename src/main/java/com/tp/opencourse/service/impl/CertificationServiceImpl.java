package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.response.CertificationResponse;
import com.tp.opencourse.entity.Certification;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.CertificationMapper;
import com.tp.opencourse.repository.CertificationRepository;
import com.tp.opencourse.service.CertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CertificationServiceImpl implements CertificationService {

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private CertificationMapper certificationMapper;

    @Override
    public CertificationResponse findCertification(String id) {
        Optional<Certification> certificationOptional = certificationRepository.findById(id);
        if(certificationOptional.isEmpty()) {
            throw new ResourceNotFoundExeption("Not found certification with id: " + id);
        }
        return certificationMapper.convertEntityToResponse(certificationOptional.get());
    }
}
