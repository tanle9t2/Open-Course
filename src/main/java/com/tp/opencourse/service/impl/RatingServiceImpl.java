package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.request.RatingRequest;
import com.tp.opencourse.dto.response.PageResponse;
import com.tp.opencourse.dto.response.RatingResponse;
import com.tp.opencourse.dto.response.RatingSummaryResponse;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Rating;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.RatingMapper;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {

    private final CourseRepository courseRepository;
    private final RegisterRepository registerRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final RatingMapper ratingMapper;

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

        if(!courseRepository.isCoursePaid(user.getUsername(), ratingRequest.getCourseId())) {
            throw new BadRequestException("Cannot rate the unregistered course");
        }

        Optional<Rating> ratingOptional = ratingRepository.isRatingExist(courseOptional.get().getId(), user.getId());
        if(ratingOptional.isPresent()) {
            Rating rating = ratingOptional.get();
            rating.setStar(ratingRequest.getStar());
            rating.setContent(ratingRequest.getContent());
            rating.setCreatedAt(LocalDateTime.now());
            ratingRepository.saveRating(rating);
            return;
        }

        RegisterDetail registerDetail = registerRepository.findProgress(user.getId(), ratingRequest.getCourseId());
        if(registerDetail.getCertification() == null) {
            throw new BadRequestException("Cannot rate the course that is not completed");
        }

        Rating rating = Rating
                .builder()
                .content(ratingRequest.getContent())
                .star(ratingRequest.getStar())
                .createdAt(LocalDateTime.now())
                .registerDetail(registerDetail)
                .build();
        registerDetail.addRating(rating);
        ratingRepository.saveRating(rating);
    }

    @Override
    public void deleteRating(String ratingId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() ->
                new ResourceNotFoundExeption("Rating doesn't exist"));

        if(!Objects.equals(rating.getRegisterDetail().getRegister().getStudent().getId(), user.getId())) {
            throw new BadRequestException("Cannot delete the rating that is not yours");
        }
        rating.getRegisterDetail().setRating(null);
        ratingRepository.delete(rating);
    }

    @Override
    public RatingSummaryResponse findRatingSummary(String courseId) {
        List<Object[]> ratingInfo = ratingRepository.findRatingSummaryByCourseId(courseId);
        Map<Integer, Integer> ratingMap = new HashMap<>();
        for(int i = 1; i <= 5; i++) {
            ratingMap.put(i, 0);
        }

        Integer totalRating = 0;
        Double averageRating = 0.0;

        for(Object[] rating : ratingInfo) {
            Integer star = ((Number) rating[0]).intValue();
            Integer starCount = ((Number) rating[1]).intValue();
            totalRating += starCount;
            averageRating += star * starCount;
            ratingMap.put(star, starCount);
        }

        if(totalRating != 0) {
            averageRating /= totalRating;
        }

        List<RatingSummaryResponse.RatingSummaryItem> ratingSummaryItems = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : ratingMap.entrySet()) {
            ratingSummaryItems.add(RatingSummaryResponse.RatingSummaryItem
                    .builder()
                    .rating(entry.getKey())
                    .percentage(totalRating != 0 ? (int) Math.round((double) entry.getValue() * 100 / totalRating) : 0)
                    .build()
            );
        }
        BigDecimal bd = new BigDecimal(averageRating);
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);

        return RatingSummaryResponse
                .builder()
                .totalRating(totalRating)
                .averageRating(bd.doubleValue())
                .ratingSummaryItems(ratingSummaryItems)
                .build();
    }

    @Override
    public PageResponse<RatingResponse> findRatingsByCourseId(String courseId, String page, String size, Integer starCount) {
        page = page == null ? "1" : page;
        size = size == null ? "3" : size;
        Integer pageInt = Integer.parseInt(page);
        Integer sizeInt = Integer.parseInt(size);
        Page<Rating> ratings = ratingRepository.findRatingByCourseId(courseId, pageInt, sizeInt, starCount);

        return PageResponse.<RatingResponse>builder()
                .content(ratings.getContent().stream().map(ratingMapper::convertEntityToResponse).toList())
                .page(ratings.getPageNumber())
                .size(ratings.getPageSize())
                .totalElements(ratings.getTotalElements())
                .totalPages(ratings.getTotalPages())
                .build();
    }

    @Override
    public RatingResponse findRatingByCourseIdAndUsername(String courseId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        Optional<Rating> rating = ratingRepository.findRatingByCourseIdAndUserId(courseId, user.getId());
        return ratingMapper.convertEntityToResponse(rating.orElse(null));
    }
}
