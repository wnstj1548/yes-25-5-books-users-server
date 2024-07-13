package com.yes255.yes255booksusersserver.presentation.controller;


import com.yes255.yes255booksusersserver.application.service.CartBookService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.DeleteCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookOrderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CartBookControllerTest {

    @Mock
    private CartBookService cartBookService;

    @InjectMocks
    private CartBookController cartBookController;

    private JwtUserDetails jwtUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUserDetails = JwtUserDetails.builder()
                .userId(1L)
                .roles(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .accessToken("token")
                .refreshToken("refreshToken")
                .build();
    }

    @DisplayName("장바구니 도서 추가 - 성공")
    @Test
    void createCartBook_success() {
        // given
        CreateCartBookRequest request = new CreateCartBookRequest(37L, 3);
        CreateCartBookResponse response = new CreateCartBookResponse(37L, 3);

        when(cartBookService.createCartBookByUserId(anyLong(), any(CreateCartBookRequest.class))).thenReturn(response);

        // when
        ResponseEntity<CreateCartBookResponse> responseEntity = cartBookController.createCartBook(request, jwtUserDetails);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        assertEquals("Bearer token", responseEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals("refreshToken", responseEntity.getHeaders().getFirst("Refresh-Token"));
    }

    @DisplayName("장바구니 도서 수정 - 성공")
    @Test
    void updateCartBook_success() {
        // given
        UpdateCartBookRequest request = new UpdateCartBookRequest(3);
        UpdateCartBookResponse response = new UpdateCartBookResponse(207L, 1);

        when(cartBookService.updateCartBookByUserId(anyLong(), anyLong(), any(UpdateCartBookRequest.class))).thenReturn(response);

        // when
        ResponseEntity<UpdateCartBookResponse> responseEntity = cartBookController.updateCartBook(1L, request, jwtUserDetails);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @DisplayName("장바구니 도서 삭제 - 성공")
    @Test
    void deleteCartBook_success() {
        // given
        DeleteCartBookResponse response = new DeleteCartBookResponse(1L);

        when(cartBookService.deleteCartBookByUserIdByCartBookId(anyLong(), anyLong())).thenReturn(response);

        // when
        ResponseEntity<DeleteCartBookResponse> responseEntity = cartBookController.deleteCartBook(1L, jwtUserDetails);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }
// CartBookResponse
// (Long userId, Long cartBookId, Long bookId, String bookName, BigDecimal bookPrice, int cartBookQuantity, Boolean bookIsPackable, String bookImage)
//    @DisplayName("장바구니 도서 목록 조회 - 성공")
//    @Test
//    void getCartBooks_success() {
//        // given
//        List<CartBookResponse> responses = List.of(
//                new CartBookResponse(8L, 207L, 38L, "조선왕조 MBTI 실록 (조선을 이끈 인물들의 반전 있는 MBTI 이야기)", new BigDecimal("15300.00"), 1, true, "http://image.toast.com/aaaacuf/yes25-5-images/mbti.jpeg"),
//                new CartBookResponse(8L, 2L, 2L, "Book 2", new BigDecimal("15300.00"), 1, true, "http://image.toast.com/aaaacuf/yes25-5-images/mbti.jpeg")
//        );
//
//        when(cartBookService.findAllCartBookById(anyLong())).thenReturn(responses);
//
//        // when
//        ResponseEntity<List<CartBookResponse>> responseEntity = cartBookController.getCartBooks(jwtUserDetails);
//
//        // then
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(responses, responseEntity.getBody());
//    }


    //(Long bookId, int quantity)
    @DisplayName("구매 도서 장바구니 갱신 - 성공")
    @Test
    void updateCartBookOrder_success() {
        // given
        List<UpdateCartBookOrderRequest> requests = List.of(
                new UpdateCartBookOrderRequest(207L, 1),
                new UpdateCartBookOrderRequest(207L, 1)
        );

        doNothing().when(cartBookService).updateCartBookOrderByUserId(anyLong(), anyList());

        // when
        ResponseEntity<Void> responseEntity = cartBookController.updateCartBookOrder(requests, jwtUserDetails);

        // then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(cartBookService, times(1)).updateCartBookOrderByUserId(anyLong(), anyList());
    }
}
