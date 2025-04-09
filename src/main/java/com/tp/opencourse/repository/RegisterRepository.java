package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Register;

import java.util.List;
import java.util.Optional;

public interface RegisterRepository {
    long areCoursesRegisteredByUserId(String userId, List<String> course);
    void save(Register register);
    Optional<Register> findById(String id);
}
