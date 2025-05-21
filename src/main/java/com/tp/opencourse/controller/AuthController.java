package com.tp.opencourse.controller;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tp.opencourse.dto.TokenDTO;
import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.event.LoginEvent;
import com.tp.opencourse.dto.request.LoginRequest;
import com.tp.opencourse.dto.request.OAuthAuthorizationRequest;
import com.tp.opencourse.dto.request.OAuthLoginRequest;
import com.tp.opencourse.dto.request.RegisterRequest;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.AuthService;
import com.tp.opencourse.service.TokenService;
import com.tp.opencourse.utils.APIResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> studentLogin(@RequestBody LoginRequest loginRequest) {
        UserAuthDTO userAuthDTO = authService.login(loginRequest, loginRequest.getRoleType());
        simpMessagingTemplate.convertAndSend(String.format("/topic/login/%s", userAuthDTO.getId()),
                LoginEvent.builder()
                        .authentication(String.format("Bearer %s", userAuthDTO.getTokenDTO().getAccessToken()))
                        .build());
        authService.deleteAllExceptCurrentToken(userAuthDTO.getId(), userAuthDTO.getTokenDTO().getUuid());
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("da login")
                .data(userAuthDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest registerRequest)  {
        authService.register(registerRequest);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_REGISTER.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<MessageResponse> refreshToken(@RequestBody Map<String, String> params) {
        TokenDTO tokenDTO = tokenService.refreshToken(params.get("refreshToken"));
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_CREATED.name())
                .data(tokenDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        authService.logout();

        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_LOGOUT.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/oauth-url")
    public ResponseEntity<MessageResponse> getGoogleOauthUrl() {
        String url = tokenService.getOauthUrl();
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_LOGIN.name())
                .data(url)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/oauth/login")
    public ResponseEntity<MessageResponse> oauthLogin(@RequestBody OAuthAuthorizationRequest oauthAuthorizationRequest) throws IOException {
        Map<String, Object> response = tokenService.getOauthAccessToken(oauthAuthorizationRequest);

        JsonObject jsonObject = new Gson().toJsonTree(response).getAsJsonObject();
        UserAuthDTO userAuthDTO = authService.login(OAuthLoginRequest.builder()
                .name(authService.extractJsonValue(jsonObject, "names", "givenName"))
                .photo(authService.extractJsonValue(jsonObject, "photos", "url"))
                .email(authService.extractJsonValue(jsonObject, "emailAddresses", "value"))
                .build());
        simpMessagingTemplate.convertAndSend(String.format("/topic/login/%s", userAuthDTO.getId()),
                LoginEvent.builder()
                        .authentication(String.format("Bearer %s", userAuthDTO.getTokenDTO().getAccessToken()))
                        .build());
        authService.deleteAllExceptCurrentToken(userAuthDTO.getId(), userAuthDTO.getTokenDTO().getUuid());

        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_LOGIN.name())
                .data(userAuthDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
