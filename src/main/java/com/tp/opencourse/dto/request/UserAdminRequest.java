package com.tp.opencourse.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.tp.opencourse.entity.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminRequest {
    private String id;

    @NotEmpty(message = "Must not be null")
    private String username;

    @NotEmpty(message = "Must not be null")
    private String firstName;

    @NotEmpty(message = "Must not be null")
    private String lastName;

    @NotEmpty(message = "Must not be null")
    private String email;

    private String phoneNumber;

    private LocalDate dob;

    private String password;

    private Boolean sex;

    private String avt;

    private Boolean active;

    private UserType type;
}
