package com.tp.opencourse.controller;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.SubmitionService;
import com.tp.opencourse.utils.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
public class SubmitionRestController {

    @Autowired
    private SubmitionService submitionService;

    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<SubmitionDTO> getSumbition(@PathVariable("submissionId") String submissionId) {
        SubmitionDTO submitionDTO = submitionService.findById(submissionId);
        return ResponseEntity.ok(submitionDTO);
    }

    @GetMapping("/submissions")
    public ResponseEntity<PageResponseT> getSubmissionsOfCourse(
            Principal user,
            @RequestParam(value = "courseId", required = false) String courseId,
            @RequestParam(name = "page", defaultValue = FilterUtils.PAGE) String page,
            @RequestParam(name = "size", defaultValue = FilterUtils.PAGE_SIZE) String size,
            @RequestParam(name = "sortField", required = false) String field,
            @RequestParam(name = "orderBy", required = false) String orderBy) {
        PageResponseT pageResponseT = submitionService.findSubmissionsByCourseId(user.getName(), courseId, Integer.parseInt(page)
                , Integer.parseInt(size), field, orderBy);
        return ResponseEntity.ok(pageResponseT);
    }

    @PostMapping("/submission/{submitionId}/comment")
    public ResponseEntity<MessageResponse> createComment(
            Principal user,
            @PathVariable("submitionId") String submitionId
            , @RequestBody CommentDTO commentDTO) {
        MessageResponse response = submitionService.createComment(user.getName(), submitionId, commentDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/submission/comment/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") String commentId) {
        submitionService.deleteComment(commentId);
        return ResponseEntity.ok(MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully delete comment")
                .build());
    }

    @PutMapping("/submission/{submissionId}/mark")
    public ResponseEntity<MessageResponse> updateMark(
            Principal user,
            @PathVariable("submissionId") String id,
            @RequestParam("mark") String mark) {
        submitionService.updateMark(user.getName(), id, Double.parseDouble(mark));

        return ResponseEntity.ok(MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully give mark")
                .build());
    }

}
