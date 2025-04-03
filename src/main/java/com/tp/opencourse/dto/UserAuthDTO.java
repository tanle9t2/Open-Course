package com.tp.opencourse.dto;


import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Setter
@Getter
public class UserAuthDTO {
    private String id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dob;
    private boolean sex;
    private String avt;
    private Boolean isAuthenticated;
    private TokenDTO tokenDTO;
}