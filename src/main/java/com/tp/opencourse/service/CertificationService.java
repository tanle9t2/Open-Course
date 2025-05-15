package com.tp.opencourse.service;

import com.tp.opencourse.dto.response.CertificationResponse;
import com.tp.opencourse.entity.RegisterDetail;

public interface CertificationService {
    CertificationResponse findCertification(String id);

    void createCertification(RegisterDetail registerDetail);
}
