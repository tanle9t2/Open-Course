package com.tp.opencourse.service;


import com.tp.opencourse.dto.response.UserProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public interface UserService {
    UserProfileResponse getProfile();
    void updateProfile(Map<String, String> fields, MultipartFile file);
}