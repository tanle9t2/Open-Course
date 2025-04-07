package com.tp.opencourse.contronller;


import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.request.LoginRequest;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.repository.CartRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.AuthService;
import com.tp.opencourse.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepository;

    @PostMapping("/add-cart-item")
    public ResponseEntity<MessageResponse> addCartItem(@AuthenticationPrincipal User user, @RequestParam String courseId) {
        cartRepository.addCartItem(courseId, user.getId());
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("da login")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete-cart-item")
    public ResponseEntity<MessageResponse> deleteCartItem(@AuthenticationPrincipal User user, @RequestParam String courseId) {
        cartRepository.addCartItem(courseId, user.getId());
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("da login")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
