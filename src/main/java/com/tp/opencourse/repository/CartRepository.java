package com.tp.opencourse.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CartRepository {
    boolean checkCartItemExistence(String courseId, String userId);
    void addCartItem(String courseId, String userId);
    void removeCartItem(String courseId, String userId);
    void removeCartItems(List<String> courseIds, String userId);
    Set<String> getCart(String userId, Map<String, String> params);
    Set<String> getCart(String userId);
}

