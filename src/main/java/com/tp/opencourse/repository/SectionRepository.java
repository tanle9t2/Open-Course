package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Section;

import java.util.Optional;

public interface SectionRepository {
    Optional<Section> findById(String id);

    Section update(Section section);

    Section create(Section section);

    void removeSection(Section section);

}
