package com.tp.opencourse.controller;


import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/summary")
    public ResponseEntity<MessageResponse> getCartSummary() {
        var data = cartService.getCartSummary();
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Deleted successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<MessageResponse> getCart() {
        var data = cartService.getCart();
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Deleted successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/add-cart-item")
    public ResponseEntity<MessageResponse> addCartItem(@RequestParam("courseId") String courseId) {
        cartService.addCartItem(courseId);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Added successfully")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete-cart-item")
    public ResponseEntity<MessageResponse> deleteCartItem(@RequestParam("courseId") String courseId) {
        cartService.removeCartItem(courseId);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Deleted successfully")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
