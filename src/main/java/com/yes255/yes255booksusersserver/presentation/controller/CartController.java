package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CartBookService;
import com.yes255.yes255booksusersserver.application.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/carts")
public class CartController {

    private final CartService cartService;
    private final CartBookService cartBookService;

    @DeleteMapping
    public ResponseEntity<Void> deleteCart(@PathVariable Long userId) {

        cartService.deleteByUserId(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
