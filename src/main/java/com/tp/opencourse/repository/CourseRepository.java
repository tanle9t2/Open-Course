package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Course;

import java.util.List;
import java.util.Set;

public interface CourseRepository {
    boolean isCourseExisted(String courseId);
    long countTotalLecture(String courseId);
    long countTotalRegistration(String courseId);
    List<Course> findAllByIds(Set<String> courseIds);
    List<Course> findAll();
}
