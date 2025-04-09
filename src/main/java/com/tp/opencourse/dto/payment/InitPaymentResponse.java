package com.tp.opencourse.dto.payment;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InitPaymentResponse {
    private String vnpUrl;
}
