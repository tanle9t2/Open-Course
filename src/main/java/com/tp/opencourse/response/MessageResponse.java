package com.tp.opencourse.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MessageResponse {
    private HttpStatus status;
    private String message;
    private Object data;
}
