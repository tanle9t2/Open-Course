package com.tp.opencourse.repository.impl;

import com.tp.opencourse.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String CART_PREFIX = "cart";
    private final String COURSE_PREFIX = "course";

    @Override
    public boolean checkCartItemExistence(String courseId, String userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(
                getCartKey(userId),
                getCartItem(courseId)
        ));
    }

    @Override
    public void addCartItem(String courseId, String userId) {
        redisTemplate.opsForSet().add(
                getCartKey(userId),
                getCartItem(courseId)
        );
    }

    @Override
    public void removeCartItem(String courseId, String userId) {
        redisTemplate.opsForSet().remove(
                getCartKey(userId),
                getCartItem(courseId)
        );
    }

    @Override
    public void getCart(String userId) {
        redisTemplate.opsForSet().
    }

    private String getCartKey(String userId) {
        return String.format("%s:%s", CART_PREFIX, userId);
    }

    private String getCartItem(String courseId) {
        return String.format("%s:%s", COURSE_PREFIX, courseId);
    }
}


//cart:user-id

