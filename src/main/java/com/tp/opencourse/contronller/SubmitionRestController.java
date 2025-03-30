package com.tp.opencourse.contronller;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.SubmitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SubmitionRestController {

    @Autowired
    private SubmitionService submitionService;

    @GetMapping("/submition/{submitionId}")
    public ResponseEntity<SubmitionDTO> getSumbition(@PathVariable("submitionId") String submitionId) {
        SubmitionDTO submitionDTO = submitionService.findById(submitionId);
        return ResponseEntity.ok(submitionDTO);
    }

    @PostMapping("/submition/{submitionId}/comment")
    public ResponseEntity<MessageResponse> createComment(@PathVariable("submitionId") String submitionId
            , @RequestBody CommentDTO commentDTO) {
        submitionService.createComment(submitionId, commentDTO);
        return ResponseEntity.ok(MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully create comment")
                .build());
    }

    @DeleteMapping("/submition/comment/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") String commentId) {
        submitionService.deleteComment(commentId);
        return ResponseEntity.ok(MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully delete comment")
                .build());
    }

    @PutMapping("/submition/{submitionId}/mark")
    public ResponseEntity<MessageResponse> updateMark(@PathVariable("submitionId") String id,
                                                      @RequestParam("mark") String mark) {
        submitionService.updateMark(id, Double.parseDouble(mark));

        return ResponseEntity.ok(MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully give mark")
                .build());
    }

}
