package com.tp.opencourse.controller;

import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173") // React app URL
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @PutMapping("/section/{sectionId}")
    public ResponseEntity<MessageResponse> updateSection(@PathVariable("sectionId") String sectionId
            , @RequestBody Map<String, String> params) {
        MessageResponse response = sectionService.updateSection(sectionId, params);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/section")
    public ResponseEntity<MessageResponse> createSection(@RequestBody Map<String, String> request) {
        MessageResponse response = sectionService.createSection(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/section/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable("sectionId") String sectionID) {
        sectionService.deleteSection(sectionID);
        return ResponseEntity.ok().build();
    }
}
