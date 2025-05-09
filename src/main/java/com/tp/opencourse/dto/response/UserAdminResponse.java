package com.tp.opencourse.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tp.opencourse.entity.Role;
import com.tp.opencourse.entity.enums.UserType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminResponse {
    private String id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private LocalDate dob;
    private boolean sex;
    private String avt;
    private boolean active;
    private UserType type;
    private List<Role> roles;
}
