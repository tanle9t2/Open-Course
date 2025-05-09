package com.tp.opencourse.repository;

import com.tp.opencourse.entity.RegisterDetail;

import java.util.List;

public interface RegisterDetailRepository {
    void update(RegisterDetail registerDetail);

    Double sumRevenueOfTeacher(String teacherId);
}
