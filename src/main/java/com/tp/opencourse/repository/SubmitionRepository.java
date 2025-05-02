package com.tp.opencourse.repository;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Submition;

import java.util.List;
import java.util.Optional;

public interface SubmitionRepository {
    Optional<Submition> findById(String id);
    Optional<Submition> findByContentProcess(String contentProcessId,String username);

    List<Submition> findByContent(String contentId);

    Page<Submition> findByCourseId(String username, String courseId, int page, int size, String sortField, String order);

    Submition save(Submition submition);
}
