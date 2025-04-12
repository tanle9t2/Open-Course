package com.tp.opencourse.contronller;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.SubmitionService;
import com.tp.opencourse.utils.FilterUtils;
import org.hibernate.internal.FilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PageResponse> getSubmissionsOfCourse(
            @RequestParam(value = "courseId", required = false) String courseId,
            @RequestParam(name = "page", defaultValue = FilterUtils.PAGE) String page,
            @RequestParam(name = "size", defaultValue = FilterUtils.PAGE_SIZE) String size,
            @RequestParam(name = "sortField", required = false) String field,
            @RequestParam(name = "orderBy", required = false) String orderBy) {
        PageResponse pageResponse = submitionService.findSubmissionsByCourseId(courseId, Integer.parseInt(page)
                , Integer.parseInt(size), field, orderBy);
        return ResponseEntity.ok(pageResponse);
    }

    @PostMapping("/submission/{submitionId}/comment")
    public ResponseEntity<MessageResponse> createComment(@PathVariable("submitionId") String submitionId
            , @RequestBody CommentDTO commentDTO) {
        submitionService.createComment(submitionId, commentDTO);
        return ResponseEntity.ok(MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully create comment")
                .build());
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
    public ResponseEntity<MessageResponse> updateMark(@PathVariable("submissionId") String id,
                                                      @RequestParam("mark") String mark) {
        submitionService.updateMark(id, Double.parseDouble(mark));

        return ResponseEntity.ok(MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully give mark")
                .build());
    }

}
