package com.tp.opencourse.service;

public interface CartService {
    void addCartItem(String courseId, String userId);
    void removeCartItem(String courseId, String userId);
    void getCart(String userId);
}
