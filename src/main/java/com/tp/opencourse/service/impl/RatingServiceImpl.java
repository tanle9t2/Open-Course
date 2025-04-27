package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.request.RatingRequest;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Rating;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.RatingRepository;
import com.tp.opencourse.repository.RegisterRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.RatingService;
import com.tp.opencourse.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.ldap.ContextSourceSettingPostProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {

    private final CourseRepository courseRepository;
    private final RegisterRepository registerRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    @Override
    public void rateCourse(RatingRequest ratingRequest) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));


        if(ratingRequest.getStar() > 5 || ratingRequest.getStar() < 1) {
            throw new BadRequestException("star must be in range 1 -> 5");
        }

        Optional<Course> courseOptional = courseRepository.findById(ratingRequest.getCourseId());
        if(courseOptional.isEmpty()) {
            throw new ResourceNotFoundExeption("Course doesn't exist");
        }

        if(!courseRepository.isCoursePaid(user.getId(), ratingRequest.getCourseId())) {
            throw new BadRequestException("Cannot rate the unregistered course");
        }

        if(ratingRepository.isRatingExist(courseOptional.get().getId(), user.getId()) != 0) {
            throw new BadRequestException("The course has been rated before");
        }

        RegisterDetail registerDetail = registerRepository.findProgress(user.getId(), ratingRequest.getCourseId());
        if(registerDetail.getPercentComplete() != 100) {
            throw new BadRequestException("Cannot rate the course that is not completed");
        }

        Rating rating = Rating
                .builder()
                .content(ratingRequest.getContent())
                .star(ratingRequest.getStar())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
        courseOptional.get().addRating(rating);
        ratingRepository.saveRating(rating);
    }

    @Override
    public boolean isRateCourse(RatingRequest ratingRequest) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        Optional<Course> courseOptional = courseRepository.findById(ratingRequest.getCourseId());
        if(courseOptional.isEmpty()) {
            throw new ResourceNotFoundExeption("Course doesn't exist");
        }
        return ratingRepository.isRatingExist(courseOptional.get().getId(), user.getId()) != 0;
    }
}
