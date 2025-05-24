package com.tp.opencourse.dto.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class LoginEvent {
    private String authentication;
}
