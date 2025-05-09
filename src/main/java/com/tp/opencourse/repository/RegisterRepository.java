package com.tp.opencourse.repository;

import com.tp.opencourse.dto.response.LearningResponse;
import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.entity.enums.RegisterStatus;

import java.util.List;
import java.util.Optional;

public interface RegisterRepository {
    long areCoursesRegisteredByUserId(String userId, List<String> course);
    void save(Register register);
    void update(Register register);
    Optional<Register> findById(String id);
    List<Register> findAllRegisteredCourse(String userId, RegisterStatus status);
    RegisterDetail findProgress(String userId, String courseId);
    List<RegisterDetail> findAllLearnings(String userId);
    Long countTotalRegistration();
    Double countTotalRevenue();
}
