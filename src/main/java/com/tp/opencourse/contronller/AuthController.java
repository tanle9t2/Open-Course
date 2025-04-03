package com.tp.opencourse.contronller;


import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.request.LoginRequest;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> login(@RequestBody LoginRequest loginRequest) {
        UserAuthDTO userAuthDTO = authService.login(loginRequest);

        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("da login")
                .data(userAuthDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
