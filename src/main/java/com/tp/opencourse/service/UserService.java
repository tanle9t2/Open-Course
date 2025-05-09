package com.tp.opencourse.service;


import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.dto.response.TeacherRevenueResponse;
import com.tp.opencourse.dto.request.UserAdminRequest;
import com.tp.opencourse.dto.response.PageResponse;
import com.tp.opencourse.dto.response.UserAdminResponse;
import com.tp.opencourse.dto.response.UserProfileResponse;
import com.tp.opencourse.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import java.util.List;

@Service
public interface UserService {

    UserProfileResponse getProfile();

    PageResponseT<TeacherRevenueResponse> getAllProfileTeacher(String kw, int page, int size);

    UserAdminResponse findById(String id);

    PageResponse<UserAdminResponse> findAll(String keyword, Integer page, Integer size, String sortBy, String direction);

    List<UserAuthDTO> findAllStudentInCourse(String courseId);

    User updateProfile(Map<String, String> fields, MultipartFile file);

    void updateUser(UserAdminRequest userRequest, List<String> roleNames, MultipartFile multipartFile) throws IOException;

}