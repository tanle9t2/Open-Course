package com.tp.opencourse.repository;

public interface CartRepository {
    boolean checkCartItemExistence(String courseId, String userId);
    void addCartItem(String courseId, String userId);
    void removeCartItem(String courseId, String userId);
    void getCart(String userId);
}

