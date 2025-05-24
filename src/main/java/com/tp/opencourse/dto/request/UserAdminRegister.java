package com.tp.opencourse.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminRegister {
    @NotEmpty(message = "Must not be null")
    private String username;

    @NotEmpty(message = "Must not be null")
    private String firstName;

    @NotEmpty(message = "Must not be null")
    private String lastName;

    @NotEmpty(message = "Must not be null")
    private String email;

    @NotEmpty(message = "Must not be null")
    private String password;
}
