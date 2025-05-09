package com.tp.opencourse.service;

public interface CourseDataSyncService {

    void createCourse(String courseId);
    void updateCourse(String courseId);
    void deleteCourse(String courseId);

    void updateCategory(String courseId);
//    void deleteCategory(String courseId);

    void updateSection(String courseId, String sectionId);
    void deleteSection(String sectionId);

    void updateContent(String sectionId, String contentId);
    void deleteContent(String contentId);
}
