package com.tp.opencourse.repository;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Submition;

import java.util.List;
import java.util.Optional;

public interface SubmitionRepository {
    Optional<Submition> findById(String id);
    List<Submition> findByContent(String contentId);
    Page<Submition> findByCourseId(String courseId,int page,int size,String sortField,String order);
    void update(Submition submition);
}
