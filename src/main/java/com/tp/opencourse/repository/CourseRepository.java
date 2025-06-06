package com.tp.opencourse.repository;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.enums.CourseStatus;

import java.util.List;
import java.util.Set;
import java.util.Optional;

public interface CourseRepository {
    boolean isCourseExisted(String courseId);

    long countTotalLecture(String courseId);

    long count();

    long countTotalRegistration(String courseId);

    List<Course> findAllByIds(Set<String> courseIds);

    List<Course> findAll();

    Page<Course> findAll(String keyword, int page, int size, String sortBy, String direction);

    long countInStatus(String kw, CourseStatus status);

    long countByKw(String kw);

    Page<Course> findAllInActive(String keyword, int page, int size, String sortBy, String direction);

    Optional<Course> findById(String id);

    Course create(Course course);

    Course update(Course course);

    void delete(String id);

    Page<Course> findByTeacherId(String id, String kw, int page, int limit);

    List<Course> findByTeacherId(String id);

    Long countByTeacherId(String id, String kw);

    boolean isCourseRegistered(String userId, String courseId);

    boolean isCoursePaid(String username, String courseId);

}
