package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Certification;

import java.util.List;
import java.util.Optional;

public interface CertificationRepository {
    Optional<Certification> findById(String id);

    Optional<Certification> findByIdRegisterId(String rgId);

    Certification save(Certification certification);
//    List<String> findAllByUserId(String id);
}
