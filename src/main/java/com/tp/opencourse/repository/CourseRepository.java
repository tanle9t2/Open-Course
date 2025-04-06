package com.tp.opencourse.repository;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findById(String id);

    Course create(Course course);

    Course update(Course course);

    void delete(String id);

    Page<Course> findByTeacherId(String id, int page, int limit);

    Long countByTeacherId(String id);
}
