package com.tp.opencourse.scheduler;


import co.elastic.clients.elasticsearch.core.UpdateRequest;
import com.tp.opencourse.dto.document.CourseDocument;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.RatingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Schedulizer {

    private final CourseRepository courseRepository;
    private final RatingRepository ratingRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    // Runs every 10 seconds
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void synchorizeRatingAndLectureAndRegistration() {


        List<UpdateQuery> updates = new ArrayList<>();

        List<Course> courses = courseRepository.findAll();
        for (Course course : courses) {

            Object[] ratingInfoDetail = ratingRepository.countRatingAverageAndQty(course.getId());
            CourseDocument.RatingDocument ratingInfo = CourseDocument.RatingDocument.builder()
                    .average((Double) ratingInfoDetail[1])
                    .qty((Long) ratingInfoDetail[0])
                    .build();

            //total lecture
            long totalLecture = courseRepository.countTotalLecture(course.getId());

            //total registration
            long totalRegistration = courseRepository.countTotalRegistration(course.getId());

            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put("ratingDocument", ratingInfo);
            updateFields.put("totalLecture", totalLecture);
            updateFields.put("totalRegistration", totalRegistration);

            Document document = Document.from(updateFields);

            UpdateQuery updateQuery = UpdateQuery.builder(course.getId())
                    .withDocument(document)
                    .build();

            updates.add(updateQuery);
        }

        elasticsearchOperations.bulkUpdate(updates, IndexCoordinates.of("course-index"));
    }
}
