package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Section;

import java.util.List;
import java.util.Optional;

public interface SectionRepository {
    Optional<Section> findById(String id);
    List<Section> findByCourseId(String courseId);

    Section update(Section section);

    Section create(Section section);

    void removeSection(Section section);

}
