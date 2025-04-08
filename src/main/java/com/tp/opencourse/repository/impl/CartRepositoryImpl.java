package com.tp.opencourse.repository.impl;

import com.tp.opencourse.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String CART_PREFIX = "cart";
    private final String COURSE_PREFIX = "course";

    @Override
    public boolean checkCartItemExistence(String courseId, String userId) {
        return redisTemplate.opsForZSet()
                .score(getCartKey(userId), getCartItem(courseId)) != null;
    }

    @Override
    public void addCartItem(String courseId, String userId) {
        redisTemplate.opsForZSet().add(
                getCartKey(userId),
                getCartItem(courseId),
                System.currentTimeMillis()
        );
    }

    @Override
    public void removeCartItem(String courseId, String userId) {
        redisTemplate.opsForZSet().remove(
                getCartKey(userId),
                getCartItem(courseId)
        );
    }

    @Override
    public Set<String> getCart(String userId, Map<String, String> params) {
        Integer page = params != null && params.get("page") != null ? Integer.parseInt(params.get("page")) : 1;
        Integer size = params != null && params.get("size") != null ? Integer.parseInt(params.get("size")) : -1;
        int start = (page - 1) * size;
        int end = start + size;

        Set<Object> rawSet = redisTemplate.opsForZSet().reverseRange(getCartKey(userId), start, end);

        if(rawSet == null || rawSet.isEmpty()){
            return new HashSet<>();
        }

        return rawSet.stream().map(o -> {
            String s = (String) o;
            return s.split(":")[1];
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getCart(String userId) {
        return this.getCart(userId, null);
    }

    private String getCartKey(String userId) {
        return String.format("%s:%s", CART_PREFIX, userId);
    }

    private String getCartItem(String courseId) {
        return String.format("%s:%s", COURSE_PREFIX, courseId);
    }
}


