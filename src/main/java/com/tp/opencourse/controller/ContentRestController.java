package com.tp.opencourse.controller;

import com.tp.opencourse.dto.ContentProcessDTO;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.response.SubmitionReponse;
import com.tp.opencourse.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ContentRestController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/contentProcess/{contentProcessId}")
    public ResponseEntity<ContentProcessDTO> getContentProcessById(@PathVariable("contentProcessId") String contentProcessId
            , Principal user
            , @RequestParam("courseId") String courseId
            , @RequestParam("contentId") String contentId
    ) {
        ContentProcessDTO content = contentService.findById(user.getName(), courseId, contentProcessId,contentId);
        return ResponseEntity.ok(content);
    }

    @PutMapping("/contentProcess/{contentProcessId}")
    public ResponseEntity<MessageResponse> updateContentProcess(
            @PathVariable("contentProcessId") String id
            , Principal user
            , @RequestBody Map<String, String> request
    ) {
        MessageResponse content = contentService.updateContentProcess(user.getName(), id, request);
        return ResponseEntity.ok(content);
    }


    @PostMapping("/content/{contentId}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<MessageResponse> updateContent(
            Principal user,
            @PathVariable("contentId") String id,
            @RequestParam Map<String, String> fields,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        MessageResponse response = contentService.updateContent(user.getName(), id, fields, file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/content")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<MessageResponse> createContent(
            Principal user,
            @RequestParam Map<String, String> fields,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        MessageResponse response = contentService.createContent(user.getName(), fields, file);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/content/{contentId}/submition")
    public ResponseEntity<SubmitionReponse> getSumbition(@PathVariable("contentId") String id) {
        SubmitionReponse submitionReponse = contentService.findSubmition(id);
        return ResponseEntity.ok(submitionReponse);
    }


    @PostMapping("/content/subContent")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<MessageResponse> createSubContent(
            Principal user,
            @RequestParam Map<String, String> field,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        MessageResponse messageResponse = contentService.createSubContent(user.getName(), field, file);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/content/{contentId}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<MessageResponse> createExercise(
            Principal user,
            @PathVariable("contentId") String id) {
        contentService.remove(user.getName(), id);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(null)
                .message("Successfully remove content")
                .status(HttpStatus.OK)
                .build());
    }

    @DeleteMapping("/content/sub/{subId}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<MessageResponse> deleteSubContent(
            Principal user,
            @PathVariable("subId") String id) {
        MessageResponse response = contentService.removeSubContent(user.getName(), id);
        return ResponseEntity.ok(response);
    }
}
