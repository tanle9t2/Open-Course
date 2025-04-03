package com.tp.opencourse.contronller;

import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.ContentService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TestController {
    @Autowired
    private ContentService contentService;

    @PostMapping("/test")
    public ResponseEntity<MessageResponse> createVideo(@RequestParam Map<String, String> field,
                                                          @RequestParam("file") MultipartFile file) throws IOException {
        contentService.createContent(field, file);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(null)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/secure")
    public ResponseEntity<MessageResponse> secure() throws IOException {
        return ResponseEntity.ok(MessageResponse.builder()
                .data("Xin chao")
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }
}
