package com.tp.opencourse.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.tp.opencourse.entity.enums.UserType;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Date;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminRequest {
    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private LocalDate dob;

    private String password;

    private Boolean sex;

    private String avt;

    private Boolean active;

    private UserType type;
}
