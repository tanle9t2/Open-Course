package com.tp.opencourse.service;

import com.google.gson.JsonObject;
import com.tp.opencourse.dto.TokenDTO;
import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.request.*;
import com.tp.opencourse.entity.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface AuthService {
    List<Role> getRoles();
    Authentication mvcLogin(AdminLoginRequest adminLoginRequest);
    UserAuthDTO login(LoginRequest loginRequest, String roleType);
    UserAuthDTO login(OAuthLoginRequest loginRequest);
    TokenDTO changePassword(String newPassword, String oldPassword, Boolean isLogAllOut);
    void register(RegisterRequest registerRequest);
    void register(UserAdminRegister userAdminRequest);
    void logout();
    String extractJsonValue(JsonObject jsonObject, String arrayName, String field);
    void deleteAllExceptCurrentToken(String userId, String uuid);

}