package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CartBookService;
import com.yes255.yes255booksusersserver.application.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "장바구니 API", description = "장바구니 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/carts")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 삭제", description = "회원 장바구니를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteCart(@PathVariable Long userId) {

        cartService.deleteByUserId(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
