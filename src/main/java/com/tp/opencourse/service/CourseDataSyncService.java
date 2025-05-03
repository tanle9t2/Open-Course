package com.tp.opencourse.service;

public interface CourseDataSyncService {

    void createCourse(String courseId);
    void updateCourse(String courseId);
    void deleteCourse(String courseId);

    void updateCategory(String courseId);
    void deleteCategory(String courseId);

    void updateSection(String courseId);
    void deleteSection(String courseId);

    void updateContent(String courseId);
    void deleteContent(String courseId);
}
