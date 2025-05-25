package com.tp.opencourse.controller;

import com.tp.opencourse.dto.response.CertificationResponse;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CertificationRestController {

    @Autowired
    private CertificationService certificationService;

    @GetMapping("/certifications/{certificationId}")
    public ResponseEntity<MessageResponse> findCertification(@PathVariable("certificationId") String certificationId) {
        CertificationResponse response = certificationService.findCertification(certificationId);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Find successfully")
                .data(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
