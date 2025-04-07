package com.tp.opencourse.service.impl;

import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.repository.CartRepository;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.maven.model.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CourseRepository courseRepository;
    @Override
    @Transactional
    public void addCartItem(String courseId, String userId) {
        if(!courseRepository.isCourseExisted(courseId)) {
            throw new ResourceNotFoundExeption("Invalid course id: " + courseId);
        }
        cartRepository.addCartItem(courseId, userId);
    }

    @Override
    @Transactional
    public void removeCartItem(String courseId, String userId) {
        if(!courseRepository.isCourseExisted(courseId)) {
            throw new ResourceNotFoundExeption("Invalid course id: " + courseId);
        }
        if(!cartRepository.checkCartItemExistence(courseId, userId)) {
            throw new ResourceNotFoundExeption("Course doesn't exist in cart");

        }
        cartRepository.addCartItem(courseId, userId);
    }

    @Override
    public void getCart(String userId) {

    }
}
