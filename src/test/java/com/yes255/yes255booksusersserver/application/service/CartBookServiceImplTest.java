package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.CartBookServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.*;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartBookServiceImplTest {

    @Mock
    private JpaCartRepository cartRepository;

    @Mock
    private JpaCartBookRepository cartBookRepository;

    @Mock
    private JpaBookRepository bookRepository;

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private CartBookServiceImpl cartBookService;

    private User testUser;
    private Cart testCart;
    private Book testBook;
    private CartBook testCartBook;

    @BeforeEach
    void setUp() {
        // Customer 설정
        Customer testCustomer = Customer.builder()
                .userId(1L)
                .userRole("USER")
                .build();

        // User 설정
        testUser = User.builder()
                .customer(testCustomer)
                .userName("Test User")
                .userPhone("010-1234-5678")
                .userEmail("testuser@example.com")
                .userBirth(LocalDate.of(1990, 1, 1))
                .userRegisterDate(LocalDateTime.now())
                .userPassword("password")
                .build();

        // Cart 설정
        testCart = Cart.builder()
                .cartId(1L)
                .cartCreatedAt(LocalDate.now())
                .customer(testCustomer)
                .build();

        // Book 설정
        testBook = Book.builder()
                .bookId(1L)
                .bookName("Test Book")
                .bookPrice(BigDecimal.valueOf(20.0))
                .build();

        // CartBook 설정
        testCartBook = CartBook.builder()
                .cartBookId(1L)
                .cart(testCart)
                .book(testBook)
                .bookQuantity(1)
                .cartBookCreatedAt(LocalDateTime.now())
                .build();
    }

    @DisplayName("장바구니에 도서 추가 - 성공")
    @Test
    void testCreateCartBookByUserId_Success() {

        CreateCartBookRequest request = CreateCartBookRequest.builder()
                .bookId(testBook.getBookId())
                .bookQuantity(2)
                .build();

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.of(testBook));
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomer_UserId(testUser.getUserId())).thenReturn(testCart);
        when(cartBookRepository.findByCart_CartIdAndBook_BookId(testCart.getCartId(), testBook.getBookId())).thenReturn(null);
        when(cartBookRepository.save(any(CartBook.class))).thenReturn(testCartBook);

        CreateCartBookResponse response = cartBookService.createCartBookByUserId(testUser.getUserId(), request);

        assertNotNull(response);
        assertEquals(testCartBook.getCartBookId(), response.cartBookId());
        assertEquals(request.quantity(), response.bookQuantity());
    }

    @DisplayName("장바구니에 도서 추가 - 실패 (알맞은 책을 찾을 수 없음)")
    @Test
    void testCreateCartBookByUserId_BookNotFound() {

        CreateCartBookRequest request = CreateCartBookRequest.builder()
                .bookId(testBook.getBookId())
                .bookQuantity(2)
                .build();

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            cartBookService.createCartBookByUserId(testUser.getUserId(), request);
        });
    }

    @DisplayName("장바구니에 도서 추가 - 실패 (유저가 존재하지 않음)")
    @Test
    void testCreateCartBookByUserId_UserNotFound() {

        CreateCartBookRequest request = CreateCartBookRequest.builder()
                .bookId(testBook.getBookId())
                .bookQuantity(2)
                .build();

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.of(testBook));
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> {
            cartBookService.createCartBookByUserId(testUser.getUserId(), request);
        });
    }

    @DisplayName("장바구니에 도서 추가 - 실패 (카트가 존재하지 않음)")
    @Test
    void testCreateCartBookByUserId_CartNotFound() {

        CreateCartBookRequest request = CreateCartBookRequest.builder()
                .bookId(testBook.getBookId())
                .bookQuantity(2)
                .build();

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.of(testBook));
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomer_UserId(testUser.getUserId())).thenReturn(null);

        assertThrows(CartException.class, () -> {
            cartBookService.createCartBookByUserId(testUser.getUserId(), request);
        });
    }

    @DisplayName("장바구니에 도서 추가 - 실패 (장바구니에 같은 도서가 이미 존재함)")
    @Test
    void testCreateCartBookByUserId_DuplicateBookInCart() {

        CreateCartBookRequest request = CreateCartBookRequest.builder()
                .bookId(testBook.getBookId())
                .bookQuantity(2)
                .build();

        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.of(testBook));
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomer_UserId(testUser.getUserId())).thenReturn(testCart);
        when(cartBookRepository.findByCart_CartIdAndBook_BookId(testCart.getCartId(), testBook.getBookId())).thenReturn(testCartBook);

        assertThrows(CartBookException.class, () -> {
            cartBookService.createCartBookByUserId(testUser.getUserId(), request);
        });
    }

    @DisplayName("장바구니 도서 수정 - 성공 케이스")
    @Test
    void testUpdateCartBookByUserId_Success() {

        UpdateCartBookRequest request = UpdateCartBookRequest.builder()
                .cartBookId(testCartBook.getCartBookId())
                .bookQuantity(3)
                .build();

        when(cartRepository.findByCustomer_UserId(testUser.getUserId())).thenReturn(testCart);
        when(cartBookRepository.findByCartBookIdAndCart_CartId(request.cartBookId(), testCart.getCartId())).thenReturn(testCartBook);

        UpdateCartBookResponse response = cartBookService.updateCartBookByUserId(testUser.getUserId(), request);

        assertNotNull(response);
        assertEquals(testCartBook.getCartBookId(), response.cartBookId());
        assertEquals(request.bookQuantity(), response.bookQuantity());
    }

    @DisplayName("장바구니 도서 수정 - 실패 (카트가 존재하지 않음)")
    @Test
    void testUpdateCartBookByUserId_CartNotFound() {

        UpdateCartBookRequest request = UpdateCartBookRequest.builder()
                .cartBookId(testCartBook.getCartBookId())
                .bookQuantity(3)
                .build();

        when(cartRepository.findByCustomer_UserId(testUser.getUserId())).thenReturn(null);

        assertThrows(CartException.class, () -> {
            cartBookService.updateCartBookByUserId(testUser.getUserId(), request);
        });
    }

    @DisplayName("장바구니 도서 수정 - 실패 (장바구니 도서가 존재하지 않음)")
    @Test
    void testUpdateCartBookByUserId_CartBookNotFound() {

        UpdateCartBookRequest request = UpdateCartBookRequest.builder()
                .cartBookId(testCartBook.getCartBookId())
                .bookQuantity(3)
                .build();

        when(cartRepository.findByCustomer_UserId(testUser.getUserId())).thenReturn(testCart);
        when(cartBookRepository.findByCartBookIdAndCart_CartId(request.cartBookId(), testCart.getCartId())).thenReturn(null);

        assertThrows(CartBookException.class, () -> {
            cartBookService.updateCartBookByUserId(testUser.getUserId(), request);
        });
    }

    @DisplayName("장바구니 도서 삭제 - 성공")
    @Test
    void testDeleteCartBookByUserIdByCartBookId_Success() {

        assertDoesNotThrow(() -> {
            cartBookService.deleteCartBookByUserIdByCartBookId(testUser.getUserId(), testCartBook.getCartBookId());
        });
    }

    @DisplayName("장바구니 도서 목록 조회 - 성공")
    @Test
    void testFindAllCartBookById_Success() {

        List<CartBook> cartBooks = new ArrayList<>();
        cartBooks.add(testCartBook);

        when(cartRepository.findByCustomer_UserId(testUser.getUserId())).thenReturn(testCart);
        when(cartBookRepository.findByCart_CartIdOrderByCartBookCreatedAtDesc(testCart.getCartId())).thenReturn(cartBooks);

        List<CartBookResponse> responses = cartBookService.findAllCartBookById(testUser.getUserId());

        assertNotNull(responses);
        assertEquals(1, responses.size());

        CartBookResponse response = responses.getFirst();
        assertEquals(testUser.getUserId(), response.userId());
        assertEquals(testCartBook.getCartBookId(), response.cartBookId());
        assertEquals(testBook.getBookId(), response.bookId());
        assertEquals(testBook.getBookName(), response.bookName());
        assertEquals(testBook.getBookPrice(), response.bookPrice());
        assertEquals(testCartBook.getBookQuantity(), response.cartBookQuantity());
    }

    @DisplayName("장바구니 도서 목록 조회 - 실패 (카트가 존재하지 않음)")
    @Test
    void testFindAllCartBookById_CartNotFound() {

        when(cartRepository.findByCustomer_UserId(testUser.getUserId())).thenReturn(null);

        assertThrows(CartException.class, () -> {
            cartBookService.findAllCartBookById(testUser.getUserId());
        });
    }
}
