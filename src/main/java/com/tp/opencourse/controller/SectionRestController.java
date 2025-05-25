package com.tp.opencourse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173") // React app URL
@PreAuthorize("hasAnyAuthority('TEACHER')")
public class SectionRestController {
    @Autowired
    private SectionService sectionService;

    @PutMapping("/section/{sectionId}")
    public ResponseEntity<MessageResponse> updateSection(
            Principal user,
            @PathVariable("sectionId") String sectionId
            , @RequestBody Map<String, String> params) {
        MessageResponse response = sectionService.updateSection(user.getName(), sectionId, params);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/section")
    public ResponseEntity<MessageResponse> createSection(
            Principal user,
            @RequestBody Map<String, String> request) throws JsonProcessingException {
        MessageResponse response = sectionService.createSection(user.getName(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/section/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            Principal user,
            @PathVariable("sectionId") String sectionID) {
        sectionService.deleteSection(user.getName(), sectionID);
        return ResponseEntity.ok().build();
    }
}
