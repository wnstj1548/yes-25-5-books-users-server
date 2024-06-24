package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CartBookService;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class CartBookController {

    private final CartBookService cartBookService;

    // 장바구니에 도서 추가
    @PostMapping("/{userId}/cart-books")
    public ResponseEntity<CreateCartBookResponse> createCartBook(@PathVariable Long userId,
                                                                 @RequestBody CreateCartBookRequest createCartBookRequest) {
        return new ResponseEntity<>(cartBookService.createCartBookByUserId(userId, createCartBookRequest), HttpStatus.CREATED);
    }

    // 장바구니 도서 수정
    @PutMapping("/{userId}/cart-books")
    public ResponseEntity<UpdateCartBookResponse> updateCartBook(@PathVariable Long userId,
                                                                 @RequestBody UpdateCartBookRequest updateCartBookRequest) {
        return new ResponseEntity<>(cartBookService.updateCartBookByUserId(userId, updateCartBookRequest), HttpStatus.OK);
    }

    // 장바구니 도서 삭제
    @DeleteMapping("/{userId}/cart-books/{cartBookId}")
    public ResponseEntity<Void> deleteCartBook(@PathVariable Long userId,
                                               @PathVariable Long cartBookId) {
        cartBookService.deleteCartBookByUserIdByCartBookId(userId, cartBookId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 장바구니 도서 목록 조회
    @GetMapping("/{userId}/cart-books")
    public ResponseEntity<List<CartBookResponse>> getCartBooks(@PathVariable Long userId) {
        return new ResponseEntity<>(cartBookService.findAllCartBookById(userId), HttpStatus.OK);
    }
}
