package com.tp.opencourse.contronller;

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
            ,@RequestBody Map<String, String> params) {
        MessageResponse response = sectionService.updateSection(sectionId, params);
        return ResponseEntity.ok(response);
    }
}
