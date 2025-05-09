package com.tp.opencourse.dto.request;


import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthAuthorizationRequest {
    private String authorizationCode;
}
