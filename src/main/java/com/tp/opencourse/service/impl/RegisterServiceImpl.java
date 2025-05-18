package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.response.CertificationResponse;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.dto.response.LearningResponse;
import com.tp.opencourse.dto.response.RegisterResponse;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.entity.enums.CourseStatus;
import com.tp.opencourse.entity.enums.PaymentStatus;
import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.ConflictException;
import com.tp.opencourse.exceptions.OverlapResourceException;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.mapper.RatingMapper;
import com.tp.opencourse.mapper.RegisterMapper;
import com.tp.opencourse.repository.*;
import com.tp.opencourse.service.RegisterService;
import com.tp.opencourse.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CartRepository cartRepository;
    private final RegisterRepository registerRepository;
    private final PaymentRepository paymentRepository;

    private final CourseMapper courseMapper;
    private final RegisterMapper registerMapper;

    @Override
    public Map<String, String> registerCourses(List<String> courseIds) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        List<Course> courses = courseRepository.findAllByIds(new HashSet<>(courseIds));

        courses.forEach(course -> {
            if (!course.isPublish() || !course.getStatus().equals(CourseStatus.ACTIVE)) {
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
                            .price(course.getPrice())
                            .course(course)
                            .register(register)
                            .build();
                }
        ).collect(Collectors.toCollection(ArrayList::new));

        cartRepository.removeCartItems(courseIds, user.getId());
        register.setRegisterDetails(registerDetails);
        registerRepository.save(register);

        return Map.of("id", String.valueOf(register.getId()),
                "amount", String.valueOf(totalAmount));
    }

    @Override
    public void cancelRegister(String registerId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        Register register = registerRepository.findById(registerId)
                .orElseThrow(() -> new ResourceNotFoundException("Course doesn't exist"));
        if(!Objects.equals(user.getId(), register.getStudent().getId())) {
            throw new AccessDeniedException("You are not allowed");
        }

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

    @Override
    public List<RegisterResponse> findAllRegisteredCourses(String status) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        RegisterStatus registerStatus = RegisterStatus.valueOf(status);
        List<Register> registers = registerRepository
                .findAllRegisteredCourse(user.getId(), registerStatus)
                .stream().sorted(Comparator.comparing(Register::getCreatedAt).reversed()).toList();

        return registers.stream().map(register -> {
            AtomicReference<Double> price = new AtomicReference<>((double) 0);
            List<CourseResponse> courses = register.getRegisterDetails().stream().map(c -> {
                price.updateAndGet(v -> (Double) (v + c.getPrice()));
                return courseMapper.convertEntityToResponse(c.getCourse());
            }).toList();

            return RegisterResponse
                    .builder()
                    .id(register.getId())
                    .price(price.get())
                    .createdAt(register.getCreatedAt())
                    .courses(courses)
                    .build();
        }).toList();
    }

    @Override
    public List<LearningResponse> findAllLearnings() {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        List<RegisterDetail> registerDetails = registerRepository.findAllLearnings(user.getId());

        return registerMapper.convertEntityToLearningResponse(registerDetails);
    }
}
