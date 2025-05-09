package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Certification;

import java.util.List;
import java.util.Optional;

public interface CertificationRepository {
    Optional<Certification> findById(String id);
//    List<String> findAllByUserId(String id);
}
