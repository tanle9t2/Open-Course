package com.tp.opencourse.dto.request;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
    private String contentId;
    private String answer;
}
