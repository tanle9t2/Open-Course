package com.tp.opencourse.service;

import com.google.gson.JsonObject;
import com.tp.opencourse.dto.TokenDTO;
import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.request.*;
import com.tp.opencourse.entity.Role;

import java.util.List;

public interface AuthService {
    List<Role> getRoles();
    UserAuthDTO login(LoginRequest loginRequest);
    UserAuthDTO login(OAuthLoginRequest loginRequest);
    TokenDTO changePassword(String newPassword, String oldPassword, Boolean isLogAllOut);
    void register(RegisterRequest registerRequest);
    void register(UserAdminRegister userAdminRequest);
    void logout();
    String extractJsonValue(JsonObject jsonObject, String arrayName, String field);

}