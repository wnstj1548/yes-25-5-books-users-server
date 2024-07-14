package com.yes255.yes255booksusersserver.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yes255.yes255booksusersserver.application.service.impl.CartBookServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.CartBookException;
import com.yes255.yes255booksusersserver.common.exception.CartException;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookOrderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
public class CartBookServiceImplTest {

    @Mock
    private JpaBookRepository bookRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private CartBookServiceImpl cartBookService;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // Book 설정
        testBook = Book.builder()
            .bookId(1L)
            .bookName("Test Book")
            .quantity(100)
            .bookPrice(BigDecimal.valueOf(20.0))
            .bookIsDeleted(false)
            .build();
    }

    @DisplayName("장바구니에 도서 추가 - 성공")
    @Test
    void testCreateCartBookByCartId_Success() {
        // given
        CreateCartBookRequest request = CreateCartBookRequest.builder()
            .bookId(testBook.getBookId())
            .quantity(2)
            .build();

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.of(testBook));
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        // when
        CreateCartBookResponse response = cartBookService.createCartBookByCartId(
            "cartId", request);

        // then
        assertNotNull(response);
    }

    @DisplayName("장바구니에 도서 추가 - 실패 (알맞은 책을 찾을 수 없음)")
    @Test
    void testCreateCartBookByCartId_BookNotFound() {
        // given
        CreateCartBookRequest request = CreateCartBookRequest.builder()
            .bookId(testBook.getBookId())
            .quantity(2)
            .build();

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.empty());

        // when && then
        assertThrows(BookNotFoundException.class, () -> {
            cartBookService.createCartBookByCartId("cartId", request);
        });
    }

    @DisplayName("장바구니에 도서 추가 - 실패 (책 재고보다 장바구니에 담을려는 수량이 더 많을 경우)")
    @Test
    void testCreateCartBookByCartId_OverQuantity() {
        // given
        CreateCartBookRequest request = CreateCartBookRequest.builder()
            .bookId(testBook.getBookId())
            .quantity(1000)
            .build();

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.of(testBook));

        // when && then
        assertThrows(CartException.class, () -> {
            cartBookService.createCartBookByCartId("cartId", request);
        });
    }

    @DisplayName("장바구니에 도서 추가 - 실패 (책이 삭제되었을 경우)")
    @Test
    void testCreateCartBookByCartId_BookDeleted() {
        // given
        CreateCartBookRequest request = CreateCartBookRequest.builder()
            .bookId(testBook.getBookId())
            .quantity(10)
            .build();

        Book deletedBook = Book.builder()
            .bookId(1L)
            .quantity(100)
            .bookIsDeleted(true)
            .build();

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.of(deletedBook));

        // when && then
        assertThrows(CartException.class, () -> {
            cartBookService.createCartBookByCartId("cartId", request);
        });
    }

    @DisplayName("장바구니에 도서 추가 - 실패 (장바구니에 같은 도서가 이미 존재함)")
    @Test
    void testCreateCartBookByCartId_DuplicateBookInCart() {
        // given
        CreateCartBookRequest request = CreateCartBookRequest.builder()
            .bookId(testBook.getBookId())
            .quantity(2)
            .build();

        Map<Long, Integer> cart = Map.of(1L, 1);

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.of(testBook));
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);

        // when && then
        assertThrows(CartBookException.class, () -> {
            cartBookService.createCartBookByCartId("cartId", request);
        });
    }

    @DisplayName("장바구니 도서 수정 - 성공 케이스")
    @Test
    void testUpdateCartBookByCartId_Success() {
        // given
        UpdateCartBookRequest request = UpdateCartBookRequest.builder()
            .quantity(3)
            .build();

        Object cart = Map.of("1", 1);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);
        doNothing().when(hashOperations).put(anyString(), anyString(), any());

        // when
        UpdateCartBookResponse response = cartBookService.updateCartBookByUserId(
            "cartId",
            1L, request);

        // then
        assertNotNull(response);
    }

    @DisplayName("장바구니 도서 수정 - 실패 (카트가 존재하지 않음)")
    @Test
    void testUpdateCartBookByCartId_CartNotFound() {
        // given
        UpdateCartBookRequest request = UpdateCartBookRequest.builder()
            .quantity(3)
            .build();
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        // when && then
        assertThrows(CartException.class, () -> {
            cartBookService.updateCartBookByUserId("cartId", 1L, request);
        });
    }

    @DisplayName("장바구니 도서 수정 - 실패 (장바구니 도서가 존재하지 않음)")
    @Test
    void testUpdateCartBookByUserId_CartBookNotFound() {
        // given
        UpdateCartBookRequest request = UpdateCartBookRequest.builder()
            .quantity(3)
            .build();

        Object cart = Map.of("1", 1);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);

        // when && then
        assertThrows(CartBookException.class, () -> {
            cartBookService.updateCartBookByUserId("cartId", 3L, request);
        });
    }

    @DisplayName("장바구니 도서 삭제 - 성공")
    @Test
    void testDeleteCartBookByUserIdByCartBookId_Success() {
        // given
        Object cart = Map.of("1", 1);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);
        doNothing().when(hashOperations).put(anyString(), anyString(), any());

        // when && then
        assertDoesNotThrow(() -> {
            cartBookService.deleteCartBookByUserIdByCartBookId("cartId",
                1L);
        });
    }

    @DisplayName("장바구니 도서 삭제 - 실패 (장바구니를 찾을 수 없을 경우)")
    @Test
    void testDeleteCartBookByUserIdByCartBookId_CartNotFound() {
        // given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        // when && then
        assertThrows(CartException.class, () -> {
            cartBookService.deleteCartBookByUserIdByCartBookId("cartId",
                1L);
        });
    }

    @DisplayName("장바구니 도서 삭제 - 실패 (장바구니에 책이 존재하지 않을 경우)")
    @Test
    void testDeleteCartBookByUserIdByCartBookId_CartBookNotFound() {
        // given
        Object cart = Map.of("1", 1);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);


        // when && then
        assertThrows(CartBookException.class, () -> {
            cartBookService.deleteCartBookByUserIdByCartBookId("cartId",
                3L);
        });
    }

    @DisplayName("장바구니 도서 목록 조회 - 성공")
    @Test
    void testFindAllCartBookById_Success() {
        // given
        Object cart = Map.of("1", 1);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(testBook));

        // when
        List<CartBookResponse> responses = cartBookService.findAllCartBookById("cartId");

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @DisplayName("장바구니 도서 목록 조회 - 성공 (장바구니가 존재하지 않을 경우)")
    @Test
    void testFindAllCartBookById_CartNotFound() {
        // given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        // when
        List<CartBookResponse> responses = cartBookService.findAllCartBookById("cartId");

        // then
        assertThat(responses).isEmpty();
    }

    @DisplayName("장바구니 도서 목록 조회 - 실패 (도서를 찾을 수 없을 경우)")
    @Test
    void testFindAllCartBookById_BookNotFound() {
        // given
        Object cart = Map.of("1", 1);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when && then
        assertThrows(BookNotFoundException.class,
            () -> cartBookService.findAllCartBookById("cartId"));
    }

    @DisplayName("장바구니 도서 수량 업데이트 - 성공")
    @Test
    void testUpdateCartBookOrderByCartId_Success() {
        // given
        List<UpdateCartBookOrderRequest> requests = Collections.singletonList(
            UpdateCartBookOrderRequest.builder()
                .bookId(testBook.getBookId())
                .quantity(1)
                .cartId("cartId")
                .build());

        Map<String, Integer> cart = new HashMap<>();
        cart.put("1", 2);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);
        when(bookRepository.existsById(anyLong())).thenReturn(true);

        // when
        cartBookService.updateCartBookOrderByUserId(requests);

        // then
        verify(hashOperations).put("cart", "cartId", Map.of(1L, 1));
    }

    @DisplayName("장바구니 도서 수량 업데이트 - 실패 (도서를 찾을 수 없을 경우)")
    @Test
    void testUpdateCartBookOrderByCartId_BookNotFound() {
        // given
        List<UpdateCartBookOrderRequest> requests = Collections.singletonList(
            UpdateCartBookOrderRequest.builder()
                .bookId(testBook.getBookId())
                .quantity(1)
                .cartId("cartId")
                .build());

        when(bookRepository.existsById(anyLong())).thenReturn(false);

        // when && then
        assertThrows(BookNotFoundException.class,
            () -> cartBookService.updateCartBookOrderByUserId(requests));
    }

    @DisplayName("장바구니 도서 수량 업데이트 - 실패 (장바구니가 존재하지 않음)")
    @Test
    void testUpdateCartBookOrderByUserId_CartNotFound() {
        // given
        List<UpdateCartBookOrderRequest> requests = Collections.singletonList(
            UpdateCartBookOrderRequest.builder()
                .bookId(testBook.getBookId())
                .quantity(1)
                .cartId("cartId")
                .build());

        when(bookRepository.existsById(anyLong())).thenReturn(true);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        // when && then
        assertThrows(CartException.class, () -> {
            cartBookService.updateCartBookOrderByUserId(requests);
        });
    }

    @DisplayName("장바구니 도서 수량 업데이트 - 실패 (장바구니 도서가 존재하지 않음)")
    @Test
    void testUpdateCartBookOrderByUserId_CartBookNotFound() {
        // given
        List<UpdateCartBookOrderRequest> requests = Collections.singletonList(
            UpdateCartBookOrderRequest.builder()
                .bookId(2L)
                .quantity(1)
                .cartId("cartId")
                .build());

        Map<String, Integer> cart = new HashMap<>();
        cart.put("1", 2);

        when(bookRepository.existsById(anyLong())).thenReturn(true);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);


        // when && then
        assertThrows(CartBookException.class, () -> {
            cartBookService.updateCartBookOrderByUserId(requests);
        });
    }

    @DisplayName("장바구니 도서 수량 업데이트 - 성공 (수량이 0이면 장바구니 삭제)")
    @Test
    void testUpdateCartBookOrderByUserId_QuantityZero() {
        // given
        List<UpdateCartBookOrderRequest> requests = Collections.singletonList(
            UpdateCartBookOrderRequest.builder()
                .bookId(testBook.getBookId())
                .quantity(1)
                .cartId("cartId")
                .build());

        Map<String, Integer> cart = new HashMap<>();
        cart.put("1", 1);

        when(bookRepository.existsById(anyLong())).thenReturn(true);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(cart);

        // when
        cartBookService.updateCartBookOrderByUserId(requests);

        // then
        verify(hashOperations).put("cart", "cartId", Collections.emptyMap());
    }
}
