package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.response.CartResponse;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.OverlapResourceException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.repository.CartRepository;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.CartService;
import com.tp.opencourse.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.maven.model.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.channels.OverlappingFileLockException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    @Override
    public void addCartItem(String courseId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        if (!courseRepository.isCourseExisted(courseId)) {
            throw new ResourceNotFoundExeption("Invalid course id: " + courseId);
        }

        if (cartRepository.checkCartItemExistence(courseId, user.getId())) {
            throw new OverlapResourceException("This course has been in cart");
        }

        if(courseRepository.isCourseRegistered(user.getId(), courseId)) {
            throw new OverlapResourceException("This course has been registered");
        }

        cartRepository.addCartItem(courseId, user.getId());
    }

    @Override
    public void removeCartItem(String courseId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        if (!courseRepository.isCourseExisted(courseId)) {
            throw new ResourceNotFoundExeption("Invalid course id: " + courseId);
        }
        if (!cartRepository.checkCartItemExistence(courseId, user.getId())) {
            throw new ResourceNotFoundExeption("Course doesn't exist in cart");
        }
        cartRepository.removeCartItem(courseId, user.getId());
    }

    @Override
    public CartResponse getCart() {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        Set<String> courseId = cartRepository.getCart(user.getId());
        List<CourseResponse> courses = courseRepository.findAllByIds(courseId)
                .stream()
                .map(courseMapper::convertEntityToResponse)
                .collect(Collectors.toList());
        double totalPrice = courses.stream()
                .mapToDouble(CourseResponse::getPrice).sum();
        return CartResponse
                .builder()
                .cartItems(courses)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public CartResponse getCartSummary() {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));
        Set<String> courseId = cartRepository.getCart(user.getId(), new HashMap<>(){{
            put("page", "1");
            put("size", "5");
        }});
        List<CourseResponse> courses = courseRepository.findAllByIds(courseId)
                .stream()
                .map(courseMapper::convertEntityToResponse)
                .collect(Collectors.toList());
        double totalPrice = courses.stream()
                .mapToDouble(CourseResponse::getPrice).sum();

        return CartResponse
                .builder()
                .cartItems(courses)
                .totalPrice(totalPrice)
                .build();
    }
}
