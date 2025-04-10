package com.tp.opencourse.controller;


import com.tp.opencourse.dto.payment.InitPaymentRequest;
import com.tp.opencourse.dto.payment.InitPaymentResponse;
import com.tp.opencourse.dto.payment.IpnResponse;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final PaymentService paymentService;

    @PostMapping("/url")
    public ResponseEntity<MessageResponse> checkout(@RequestBody InitPaymentRequest paymentRequest) {
        InitPaymentResponse initPaymentResponse = paymentService.init(paymentRequest);
        MessageResponse messageResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("url")
                .data(initPaymentResponse)
                .build();
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping("/vnpay_ipn")
    public IpnResponse payCallbackHandler(@RequestParam Map<String, String> params) {
        return paymentService.process(params);
    }
}
