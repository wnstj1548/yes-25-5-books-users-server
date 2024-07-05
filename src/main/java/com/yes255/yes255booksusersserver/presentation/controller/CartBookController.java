package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CartBookService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.DeleteCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookOrderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 도서 장바구니 관련 API를 제공하는 CartBookController
 */

@Tag(name = "도서 장바구니 API", description = "도서 장바구니 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class CartBookController {

    private final CartBookService cartBookService;

    /**
     * 장바구니에 도서를 추가합니다.
     *
     * @param createCartBookRequest 장바구니에 추가할 도서 정보 요청 객체
     * @param jwtUserDetails 유저 토큰 정보
     * @return 생성된 장바구니 도서 정보 응답 객체
     */
    @Operation(summary = "장바구니 도서 추가", description = "장바구니에 도서를 추가합니다.")
    @PostMapping("/cart-books")
    public ResponseEntity<CreateCartBookResponse> createCartBook(@RequestBody CreateCartBookRequest createCartBookRequest,
                                                                 @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();
        String accessToken = jwtUserDetails.accessToken();
        String refreshToken = jwtUserDetails.refreshToken();

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .header("Refresh-Token", refreshToken)
            .body(cartBookService.createCartBookByUserId(userId, createCartBookRequest));
    }

    /**
     * 장바구니의 특정 도서 수량을 수정합니다.
     *
     * @param updateCartBookRequest 장바구니 도서 수량 수정 요청 객체
     * @param jwtUserDetails 유저 토큰 정보
     * @return 수정된 장바구니 도서 정보 응답 객체
     */
    @Operation(summary = "장바구니 도서 수정", description = "장바구니의 특정 도서 수량을 수정합니다.")
    @PutMapping("/cart-books/books/{bookId}")
    public ResponseEntity<UpdateCartBookResponse> updateCartBook(@PathVariable Long bookId,
        @RequestBody UpdateCartBookRequest updateCartBookRequest,
        @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return new ResponseEntity<>(cartBookService.updateCartBookByUserId(userId, bookId, updateCartBookRequest), HttpStatus.OK);
    }

    /**
     * 장바구니에서 특정 도서를 삭제합니다.
     *
     * @param bookId 삭제할 장바구니 내 도서의 ID
     * @param jwtUserDetails 유저 토큰 정보
     * @return No Content 상태의 응답
     */
    @Operation(summary = "장바구니 도서 삭제", description = "장바구니에서 특정 도서를 삭제합니다.")
    @DeleteMapping("/cart-books/books/{bookId}")
    public ResponseEntity<DeleteCartBookResponse> deleteCartBook(@PathVariable Long bookId,
                                               @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(cartBookService.deleteCartBookByUserIdByCartBookId(userId, bookId));
    }

    /**
     * 장바구니에 있는 모든 도서를 조회합니다.
     *
     * @param jwtUserDetails 유저 토큰 정보
     * @return 장바구니 도서 목록 응답 객체 리스트
     */
    @Operation(summary = "장바구니 도서 목록 조회", description = "장바구니에 있는 모든 도서를 조회합니다.")
    @GetMapping("/cart-books")
    public ResponseEntity<List<CartBookResponse>> getCartBooks(@CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return new ResponseEntity<>(cartBookService.findAllCartBookById(userId), HttpStatus.OK);
    }


    @Operation(summary = "구매 도서 장바구니 갱신", description = "장바구니에서 도서를 구해하면 장바구니 도서를 갱신합니다.")
    @PutMapping("/cart-books/orders")
    public ResponseEntity<Void> updateCartBookOrder(@RequestBody List<UpdateCartBookOrderRequest> cartBookRequest,
                                                                            @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        cartBookService.updateCartBookOrderByUserId(userId, cartBookRequest);

        return ResponseEntity.noContent().build();
    }
}
