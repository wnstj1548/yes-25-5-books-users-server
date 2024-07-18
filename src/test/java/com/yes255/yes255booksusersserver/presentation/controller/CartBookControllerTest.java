package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CartService;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookOrderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.DeleteCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CartBookControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartBookController cartBookController;

    private CreateCartBookRequest createCartBookRequest;
    private CreateCartBookResponse createCartBookResponse;
    private UpdateCartBookRequest updateCartBookRequest;
    private UpdateCartBookResponse updateCartBookResponse;
    private DeleteCartBookResponse deleteCartBookResponse;
    private CartBookResponse cartBookResponse;

    @BeforeEach
    void setUp() {
        createCartBookRequest = new CreateCartBookRequest(1L, 2);
        createCartBookResponse = CreateCartBookResponse.from("cart123");

        updateCartBookRequest = new UpdateCartBookRequest(3);
        updateCartBookResponse = UpdateCartBookResponse.of("cart123", 3);

        deleteCartBookResponse = DeleteCartBookResponse.from(1L);

        cartBookResponse = new CartBookResponse(1L, List.of(), "Test Book", BigDecimal.valueOf(10.99), 2, true, "test-image.png");
    }

    @Test
    @DisplayName("장바구니에 도서 추가 - 성공")
    void testCreateCartBook() {
        when(cartService.createCartBookByCartId(anyString(), any(CreateCartBookRequest.class)))
                .thenReturn(createCartBookResponse);

        ResponseEntity<CreateCartBookResponse> response = cartBookController.createCartBook(createCartBookRequest, "cart123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createCartBookResponse, response.getBody());
        verify(cartService, times(1)).createCartBookByCartId("cart123", createCartBookRequest);
    }

    @Test
    @DisplayName("장바구니 도서 수정 - 성공")
    void testUpdateCartBook() {
        when(cartService.updateCartBookByUserId(anyString(), anyLong(), any(UpdateCartBookRequest.class)))
                .thenReturn(updateCartBookResponse);

        ResponseEntity<UpdateCartBookResponse> response = cartBookController.updateCartBook(1L, "cart123", updateCartBookRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updateCartBookResponse, response.getBody());
        verify(cartService, times(1)).updateCartBookByUserId("cart123", 1L, updateCartBookRequest);
    }

    @Test
    @DisplayName("장바구니 도서 삭제 - 성공")
    void testDeleteCartBook() {
        when(cartService.deleteCartBookByUserIdByCartBookId(anyString(), anyLong()))
                .thenReturn(deleteCartBookResponse);

        ResponseEntity<DeleteCartBookResponse> response = cartBookController.deleteCartBook("cart123", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(deleteCartBookResponse, response.getBody());
        verify(cartService, times(1)).deleteCartBookByUserIdByCartBookId("cart123", 1L);
    }

    @Test
    @DisplayName("장바구니 도서 목록 조회")
    void testGetCartBooks() {
        when(cartService.findAllCartBookById(anyString())).thenReturn(Collections.singletonList(cartBookResponse));

        ResponseEntity<List<CartBookResponse>> response = cartBookController.getCartBooks("cart123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(cartBookResponse), response.getBody());
        verify(cartService, times(1)).findAllCartBookById("cart123");
    }

    @Test
    @DisplayName("구매 도서 장바구니 갱신 - 성공")
    void testUpdateCartBookOrder() {
        List<UpdateCartBookOrderRequest> cartBookRequests = List.of(
                new UpdateCartBookOrderRequest(1L, 2, "cart123"),
                new UpdateCartBookOrderRequest(2L, 3, "cart123")
        );

        doNothing().when(cartService).updateCartBookOrderByUserId(cartBookRequests);

        ResponseEntity<Void> response = cartBookController.updateCartBookOrder(cartBookRequests);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService, times(1)).updateCartBookOrderByUserId(cartBookRequests);
    }
}
