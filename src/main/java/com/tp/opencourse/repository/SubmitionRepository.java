package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Submition;

import java.util.List;
import java.util.Optional;

public interface SubmitionRepository {
    Optional<Submition> findById(String id);
    List<Submition> findByContent(String contentId);

    void update(Submition submition);
}
