package com.tp.opencourse.service;

import com.google.gson.JsonObject;
import com.tp.opencourse.dto.TokenDTO;
import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.request.LoginRequest;
import com.tp.opencourse.dto.request.OAuthLoginRequest;
import com.tp.opencourse.dto.request.RegisterRequest;

public interface AuthService {
    UserAuthDTO login(LoginRequest loginRequest);
    UserAuthDTO login(OAuthLoginRequest loginRequest);
    TokenDTO changePassword(String newPassword, String oldPassword, Boolean isLogAllOut);
    void register(RegisterRequest registerRequest);
    void logout();
    String extractJsonValue(JsonObject jsonObject, String arrayName, String field);
}