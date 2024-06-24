package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CartBookService;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;

@Tag(name = "도서 장바구니 API", description = "도서 장바구니 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class CartBookController {

    // todo : jwt 토큰으로 변경

    private final CartBookService cartBookService;

    @Operation(summary = "장바구니 도서 추가", description = "장바구니에 도서를 추가합니다.")
    @PostMapping("/cart-books")
    public ResponseEntity<CreateCartBookResponse> createCartBook(@RequestBody CreateCartBookRequest createCartBookRequest) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(cartBookService.createCartBookByUserId(userId, createCartBookRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "장바구니 도서 수정", description = "장바구니의 특정 도서 수량을 수정합니다.")
    @PutMapping("/cart-books")
    public ResponseEntity<UpdateCartBookResponse> updateCartBook(@RequestBody UpdateCartBookRequest updateCartBookRequest) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(cartBookService.updateCartBookByUserId(userId, updateCartBookRequest), HttpStatus.OK);
    }

    @Operation(summary = "장바구니 도서 삭제", description = "장바구니에서 특정 도서를 삭제합니다.")
    @DeleteMapping("/cart-books/{cartBookId}")
    public ResponseEntity<Void> deleteCartBook(@PathVariable Long cartBookId) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        cartBookService.deleteCartBookByUserIdByCartBookId(userId, cartBookId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "장바구니 도서 목록 조회", description = "장바구니에 있는 모든 도서를 조회합니다.")
    @GetMapping("/cart-books")
    public ResponseEntity<List<CartBookResponse>> getCartBooks() {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(cartBookService.findAllCartBookById(userId), HttpStatus.OK);
    }
}
