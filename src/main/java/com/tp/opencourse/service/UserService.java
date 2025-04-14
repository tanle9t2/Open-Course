package com.tp.opencourse.service;


import com.tp.opencourse.dto.response.UserProfileResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserProfileResponse getProfile();
}