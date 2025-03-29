package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Section;

import java.util.Optional;

public interface SectionRepository {
    Optional<Section> findById(String id);

    void update(Section section);

}
