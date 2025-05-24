package com.tp.opencourse.dto.request;


import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginRequest {
    private String username, password;
}
