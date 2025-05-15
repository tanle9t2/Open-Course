package com.tp.opencourse.dto.response;


import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodStatisticResponse {
    private String label;
    private String value;
}
