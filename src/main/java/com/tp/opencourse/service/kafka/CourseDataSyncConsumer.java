package com.tp.opencourse.service.kafka;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tp.opencourse.service.CourseDataSyncService;
import com.tp.opencourse.utils.KafkaOperator;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseDataSyncConsumer {

    private final CourseDataSyncService courseDataSyncService;

    @KafkaListener(
            topics = "cdb.course.course",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void syncCourse(ConsumerRecord<?, ?> consumerRecord) {
        JsonObject json = new Gson().fromJson((String) consumerRecord.value(), JsonObject.class);
        if (json != null) {
            JsonObject payload = json.get("payload").getAsJsonObject();
            String courseId = null;
            if (payload != null) {
                String op = payload.get("op").toString().replace("\"", "");
                switch (op) {
                    case KafkaOperator.CREATE:
                        courseId = payload.get("after").getAsJsonObject().get("id").toString();
                        courseDataSyncService.createCourse(courseId);
                        break;
                    case KafkaOperator.UPDATE:
                        courseId = payload.get("after").getAsJsonObject().get("id").toString();
                        courseDataSyncService.updateCourse(courseId);
                        break;
                    case KafkaOperator.DELETE:
                        courseId = payload.get("before").getAsJsonObject().get("id").toString();
                        courseDataSyncService.deleteCourse(courseId);
                        break;
                }
            }
        }
    }

    @KafkaListener(
            topics = "cdb.course.category",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void syncCategory(ConsumerRecord<?, ?> consumerRecord) {
        JsonObject json = new Gson().fromJson((String) consumerRecord.value(), JsonObject.class);
        if (json != null) {
            JsonObject payload = json.get("payload").getAsJsonObject();
            String categoryId = null;
            if (payload != null) {
                String op = payload.get("op").toString().replace("\"", "");
                switch (op) {
                    case KafkaOperator.UPDATE:
                        categoryId = payload.get("after").getAsJsonObject().get("id").toString();
                        courseDataSyncService.updateCategory(categoryId);
                        break;
//                    case KafkaOperator.DELETE:
//                        courseId = payload.get("before").getAsJsonObject().get("id").toString();
//                        courseDataSyncService.deleteCourse(courseId);
//                        break;
                }
            }
        }
    }

    @KafkaListener(
            topics = "cdb.course.section",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void syncSection(ConsumerRecord<?, ?> consumerRecord) {
        JsonObject json = new Gson().fromJson((String) consumerRecord.value(), JsonObject.class);
        if (json != null) {
            JsonObject payload = json.get("payload").getAsJsonObject();
            String sectionId = null;
            if (payload != null) {
                String op = payload.get("op").toString().replace("\"", "");
                switch (op) {
                    case KafkaOperator.UPDATE:
                        sectionId = payload.get("after").getAsJsonObject().get("id").toString();
                        courseDataSyncService.updateSection(sectionId);
                        break;
                    case KafkaOperator.DELETE:
                        sectionId = payload.get("before").getAsJsonObject().get("id").toString();
                        courseDataSyncService.deleteSection(sectionId);
                        break;
                }
            }
        }
    }

    @KafkaListener(
            topics = "cdb.course.content",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void syncContent(ConsumerRecord<?, ?> consumerRecord) {
        JsonObject json = new Gson().fromJson((String) consumerRecord.value(), JsonObject.class);
        if (json != null) {
            JsonObject payload = json.get("payload").getAsJsonObject();
            String courseId = null;
            if (payload != null) {
                String op = payload.get("op").toString().replace("\"", "");
                switch (op) {
                    case KafkaOperator.UPDATE:
                        courseId = payload.get("after").getAsJsonObject().get("id").toString();
                        courseDataSyncService.updateCourse(courseId);
                        break;
                    case KafkaOperator.DELETE:
                        courseId = payload.get("before").getAsJsonObject().get("id").toString();
                        courseDataSyncService.deleteCourse(courseId);
                        break;
                }
            }
        }
    }
}
