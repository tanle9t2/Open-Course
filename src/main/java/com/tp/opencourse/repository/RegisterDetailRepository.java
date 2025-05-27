package com.tp.opencourse.repository;

import com.tp.opencourse.entity.RegisterDetail;

import java.util.List;

public interface RegisterDetailRepository {
    void update(RegisterDetail registerDetail);

    List<RegisterDetail> findAllByCourseId(String courseId);

    Double sumRevenueOfTeacher(String teacherId);
}
