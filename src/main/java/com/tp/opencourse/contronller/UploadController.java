package com.tp.opencourse.contronller;

import com.tp.opencourse.dto.FileDTO;
import com.tp.opencourse.dto.VideoDTO;
import com.tp.opencourse.entity.Video;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/video")
    public ResponseEntity<MessageResponse> uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        VideoDTO videoDTO = cloudinaryService.uploadVideo(file);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(videoDTO)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("/file")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        FileDTO fileDTO = cloudinaryService.uploadFile(file);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(fileDTO)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }
}
