package com.tp.opencourse.dto.payment;


import lombok.Data;

@Data
public class InitPaymentRequest {
    private String txnRef; //orderId
    private Integer amount; //totalOrderIdAmount
    private String ipAddress; //ipaddress
}
