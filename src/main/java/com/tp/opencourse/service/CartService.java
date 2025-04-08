package com.tp.opencourse.service;

import com.tp.opencourse.dto.response.CartResponse;

import java.util.Set;

public interface CartService {
    void addCartItem(String courseId);
    void removeCartItem(String courseId);
    CartResponse getCart();
    CartResponse getCartSummary();
}
