package com.tp.opencourse.service;

import com.tp.opencourse.dto.response.CertificationResponse;

public interface CertificationService {
    CertificationResponse findCertification(String id);
}
