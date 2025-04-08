package com.tp.opencourse.contronller;

import com.google.protobuf.Message;
import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.dto.ContentProcessDTO;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.response.SubmitionReponse;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ContentRestController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/content/{contentId}")
    public ResponseEntity<ContentProcessDTO> getContentById(@PathVariable("contentId") String id
            , @RequestParam("userId") String userId
            , @RequestParam("courseId") String courseId
    ) {
        ContentProcessDTO content = contentService.findById(userId, courseId, id);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/contents/{contentId}")
    public ResponseEntity<ContentDTO> test(@PathVariable("contentId") String id

    ) {
        ContentDTO content = contentService.get(id);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/content/{contentId}/submition")
    public ResponseEntity<SubmitionReponse> getSumbition(@PathVariable("contentId") String id) {
        SubmitionReponse submitionReponse = contentService.findSubmition(id);
        return ResponseEntity.ok(submitionReponse);
    }

    @PostMapping("/content")
    public ResponseEntity<MessageResponse> createExercise(@RequestParam Map<String, String> field,
                                                          @RequestParam("file") MultipartFile file) throws IOException {
        contentService.createExercise(field, file);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(null)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("/content/subContent")
    public ResponseEntity<MessageResponse> createSubContent(@RequestParam Map<String, String> field,
                                                            @RequestParam("file") MultipartFile file) throws IOException {
        contentService.createSubContent(field, file);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(null)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @DeleteMapping("/content/{contentId}")
    public ResponseEntity<MessageResponse> createExercise(@PathVariable("contentId") String id) {
        contentService.remove(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(null)
                .message("Successfully remove content")
                .status(HttpStatus.OK)
                .build());
    }
}
