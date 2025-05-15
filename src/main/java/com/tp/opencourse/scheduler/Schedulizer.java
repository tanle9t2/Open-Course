package com.tp.opencourse.scheduler;


import co.elastic.clients.elasticsearch.core.UpdateRequest;
import com.tp.opencourse.dto.document.CourseDocument;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Payment;
import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.entity.enums.PaymentStatus;
import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.PaymentRepository;
import com.tp.opencourse.repository.RatingRepository;
import com.tp.opencourse.repository.RegisterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class Schedulizer {

    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final RegisterRepository registerRepository;
    private final RatingRepository ratingRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    // Runs every 10 seconds
    @Scheduled(cron = "*/60 * * * * *")
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
            updateFields.put("totalLecture", Optional.of(totalLecture));
            updateFields.put("totalRegistration", Optional.of(totalRegistration));

            Document document = Document.from(updateFields);

            UpdateQuery updateQuery = UpdateQuery.builder(course.getId())
                    .withDocument(document)
                    .build();

            updates.add(updateQuery);
        }

        elasticsearchOperations.bulkUpdate(updates, IndexCoordinates.of("course-index"));
    }

    @Scheduled(cron = "*/60 * * * * *")
    @Transactional
    public void cancelPaymentWaitingAfter48hrs() {

        List<Register> registers = registerRepository.findAll();
        registers.forEach(register -> {
            if(register.getStatus().equals(RegisterStatus.PAYMENT_WAITING)) {
                LocalDateTime target = register.getCreatedAt(); // your LocalDateTime value
                LocalDateTime now = LocalDateTime.now();

                if(target.isBefore(now.plusHours(48))) {
                    Double totalAmount = register
                            .getRegisterDetails().stream().map(RegisterDetail::getPrice).mapToDouble(Double::doubleValue).sum();

                    Payment payment = Payment
                            .builder()
                            .price(totalAmount)
                            .status(PaymentStatus.FAIL)
                            .createdAt(LocalDateTime.now())
                            .register(register)
                            .build();

                    register.setStatus(RegisterStatus.FAILED);
                    register.addPayment(payment);

                    paymentRepository.save(payment);
                    registerRepository.update(register);
                }
            }
        });
        registerRepository.saveAll(registers);
    }

}
