package com.tp.opencourse.service.impl;

import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.ConflictException;
import com.tp.opencourse.exceptions.OverlapResourceException;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.RegisterRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.RegisterService;
import com.tp.opencourse.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final RegisterRepository registerRepository;

    @Override
    @Transactional
    public Map<String, String> registerCourses(List<String> courseIds) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        List<Course> courses = courseRepository.findAllByIds(new HashSet<>(courseIds));

        courses.forEach(course -> {
            if (!course.isPublish()) {
                throw new ConflictException("Some courses are unpublished");
            }
        });

        if (courses.size() != courseIds.size()) {
            throw new BadRequestException("Course Ids s");
        }

        long isRegistered = registerRepository.areCoursesRegisteredByUserId(user.getId(), courseIds);
        if (isRegistered != 0) {
            throw new OverlapResourceException("Courses have been registered");
        }
        Register register = Register
                .builder()
                .status(RegisterStatus.PAYMENT_WAITING)
                .createdAt(LocalDateTime.now())
                .student(user)
                .build();

        AtomicInteger totalAmount = new AtomicInteger();
        List<RegisterDetail> registerDetails = courses.stream().map(course -> {
            totalAmount.addAndGet((int) course.getPrice());
            return RegisterDetail
                    .builder()
                    .percentComplete(0)
                    .course(course)
                    .register(register)
                    .build();
            }
        ).collect(Collectors.toCollection(ArrayList::new));


        register.setRegisterDetails(registerDetails);
        registerRepository.save(register);


        return new HashMap<>() {{
            put("id", String.valueOf(register.getId()));
            put("amount", String.valueOf(totalAmount));
        }};
    }
}
