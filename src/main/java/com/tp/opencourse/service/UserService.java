package com.tp.opencourse.service;


import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.response.UserProfileResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserProfileResponse getProfile();

    List<UserAuthDTO> findAllStudentInCourse(String courseId);
}