package com.tp.opencourse.service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tp.opencourse.service.CourseDataSyncService;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.utils.KafkaOperator;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseDataSyncConsumer {

    private final CourseDataSyncService courseDataSyncService;
    private final CourseService courseService;

    @KafkaListener(
            topics = "odb.open_course.course",
            groupId = "open-course",
            containerFactory = "kafkaListenerSyncContainerFactory"
    )
    public void syncCourse(@Payload(required = false) JsonNode node) {
        if (node == null || node.get("payload") == null) return;

        JsonNode payload = node.get("payload");
        String op = payload.get("op").asText();

        String courseId = null;
        switch (op) {
            case KafkaOperator.CREATE:
                courseId = payload.path("after").path("id").asText();
                courseDataSyncService.createCourse(courseId);
                break;
            case KafkaOperator.UPDATE:
                courseId = payload.path("after").path("id").asText();
                courseDataSyncService.updateCourse(courseId);
                break;
            case KafkaOperator.DELETE:
                courseId = payload.path("before").path("id").asText();
                courseDataSyncService.deleteCourse(courseId);
                break;
        }
    }

    @KafkaListener(
            topics = "odb.open_course.category",
            groupId = "open-course",
            containerFactory = "kafkaListenerSyncContainerFactory"
    )
    public void syncCategory(@Payload(required = false) JsonNode node) {
        if (node == null || node.get("payload") == null) return;

        JsonNode payload = node.get("payload");
        String op = payload.get("op").asText();

        String categoryId = null;

        switch (op) {
            case KafkaOperator.UPDATE:
                JsonNode after = payload.get("after");
                if (after != null && after.has("id")) {
                    categoryId = after.get("id").asText();
                    courseDataSyncService.updateCategory(categoryId);
                }
                break;
        }
    }

    @KafkaListener(
            topics = "odb.open_course.section",
            groupId = "open-course",
            containerFactory = "kafkaListenerSyncContainerFactory"
    )
    public void syncSection(@Payload(required = false) JsonNode node) {
        if (node == null || node.get("payload") == null) return;

        JsonNode payload = node.get("payload");
        String op = payload.get("op").asText();

        String sectionId = null;
        JsonNode inspectedNode = null;
        switch (op) {
            case KafkaOperator.CREATE, KafkaOperator.UPDATE:
                inspectedNode = payload.get("after");
                if (inspectedNode != null && inspectedNode.has("id")) {

                    sectionId = inspectedNode.get("id").asText();
                    String courseId = inspectedNode.get("course_id").asText();
                    courseDataSyncService.updateSection(courseId, sectionId);
                }
                break;
            case KafkaOperator.DELETE:
                inspectedNode = payload.get("before");
                if (inspectedNode != null && inspectedNode.has("id")) {
                    sectionId = inspectedNode.get("id").asText();
                    courseDataSyncService.deleteSection(sectionId);
                }
                break;
        }
    }

    @KafkaListener(
            topics = "odb.open_course.content",
            groupId = "open-course",
            containerFactory = "kafkaListenerSyncContainerFactory"
    )
    public void syncContent(@Payload(required = false) JsonNode node) {
        if (node == null || node.get("payload") == null) return;

        JsonNode payload = node.get("payload");
        String op = payload.get("op").asText();

        String contentId = null;

        switch (op) {
            case KafkaOperator.CREATE, KafkaOperator.UPDATE:
                JsonNode after = payload.get("after");
                if (after != null && after.has("id") && after.has("section_id")) {
                    contentId = after.get("id").asText();
                    String sectionId = after.get("section_id").asText();
                    courseDataSyncService.updateContent(sectionId, contentId);
                }
                if (KafkaOperator.CREATE.equals(op)) {
                    courseService.updatePercentComplete(after.get("section_id").asText());
                }
                break;

            case KafkaOperator.DELETE:
                JsonNode before = payload.get("before");
                if (before != null && before.has("id")) {
                    contentId = before.get("id").asText();
                    courseDataSyncService.deleteContent(contentId);
                    courseService.updatePercentComplete(before.get("section_id").asText());
                }
                break;
        }
    }
}
