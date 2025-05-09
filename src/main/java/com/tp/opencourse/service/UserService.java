package com.tp.opencourse.service;


import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.dto.response.TeacherRevenueResponse;
import com.tp.opencourse.dto.response.UserProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import java.util.List;

@Service
public interface UserService {
    UserProfileResponse getProfile();

    PageResponseT<TeacherRevenueResponse> getAllProfileTeacher(String kw, int page, int size);

    List<UserAuthDTO> findAllStudentInCourse(String courseId);

    void updateProfile(Map<String, String> fields, MultipartFile file);
}