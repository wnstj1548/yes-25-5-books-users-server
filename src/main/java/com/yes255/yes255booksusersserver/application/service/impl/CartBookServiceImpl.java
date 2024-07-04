package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CartBookService;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.CartBookException;
import com.yes255.yes255booksusersserver.common.exception.CartException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.Cart;
import com.yes255.yes255booksusersserver.persistance.domain.CartBook;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookOrderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartBookServiceImpl implements CartBookService {

    private final JpaCartRepository cartRepository;
    private final JpaCartBookRepository cartBookRepository;
    private final JpaBookRepository bookRepository;
    private final JpaUserRepository userRepository;

    // 장바구니에 도서 추가
    @Override
    public CreateCartBookResponse createCartBookByUserId(Long userId,
        CreateCartBookRequest request) {

        Book book = bookRepository.findById(request.bookId())
            .orElseThrow(() -> new BookNotFoundException(
                ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 404, LocalDateTime.now())));

        Cart cart = cartRepository.findByCustomer_UserId(userId);

        if (Objects.isNull(cart)) {
            throw new CartException(
                ErrorStatus.toErrorStatus("카트가 존재하지 않습니다.", 404, LocalDateTime.now()));
        }

        if (cartBookRepository.existsByBookAndCart(book, cart)) {
            throw new CartBookException(ErrorStatus.toErrorStatus(
                "장바구니에 이미 같은 도서가 존재합니다.", 409, LocalDateTime.now()
            ));
        }

        CartBook cartBook = cartBookRepository.save(
            CartBook.builder()
                .book(book)
                .bookQuantity(request.quantity())
                .cart(cart)
                .cartBookCreatedAt(LocalDateTime.now())
                .build());

        return CreateCartBookResponse.builder()
            .bookId(cartBook.getBook().getBookId())
            .quantity(cartBook.getBookQuantity())
            .build();
    }

    // 장바구니에 도서 수정 (수량 조절)
    @Override
    public UpdateCartBookResponse updateCartBookByUserId(Long userId,
        UpdateCartBookRequest request) {

        Cart cart = cartRepository.findByCustomer_UserId(userId);

        if (Objects.isNull(cart)) {
            throw new CartException(
                ErrorStatus.toErrorStatus("카트가 존재하지 않습니다.", 404, LocalDateTime.now()));
        }

        CartBook cartBook = cartBookRepository.findByCartBookIdAndCart_CartId(request.cartBookId(),
            cart.getCartId());

        if (Objects.isNull(cartBook)) {
            throw new CartBookException(
                ErrorStatus.toErrorStatus("장바구니 도서가 존재하지 않습니다.", 404, LocalDateTime.now()));
        }

        cartBook.updateCartBookQuantity(request.bookQuantity());

        cartBookRepository.save(cartBook);

        return UpdateCartBookResponse.builder()
            .cartBookId(cartBook.getCartBookId())
            .bookQuantity(request.bookQuantity())
            .build();
    }

    // 장바구니에서 도서 삭제
    @Override
    public void deleteCartBookByUserIdByCartBookId(Long userId, Long cartBookId) {
        cartBookRepository.deleteById(cartBookId);
    }

    // 장바구니 도서 목록 조회
    @Override
    public List<CartBookResponse> findAllCartBookById(Long userId) {

        Cart cart = cartRepository.findByCustomer_UserId(userId);

        if (Objects.isNull(cart)) {
            throw new CartException(
                ErrorStatus.toErrorStatus("카트가 존재하지 않습니다.", 404, LocalDateTime.now()));
        }
        List<CartBook> cartBooks = cartBookRepository.findByCart_CartIdOrderByCartBookCreatedAtDesc(
            cart.getCartId());

        return cartBooks.stream()
            .map(cartBook -> new CartBookResponse(userId, cartBook.getCartBookId(),
                cartBook.getBook().getBookId(),
                cartBook.getBook().getBookName(), cartBook.getBook().getBookPrice(),
                cartBook.getBookQuantity(), cartBook.getBook().isBookIsPackable()))
            .toList();
    }

    @Transactional
    @Override
    public void updateCartBookOrderByUserId(Long userId, List<UpdateCartBookOrderRequest> request) {

        Cart cart = cartRepository.findByCustomer_UserId(userId);

        if (Objects.isNull(cart)) {
            throw new CartException(
                ErrorStatus.toErrorStatus("장바구니가 존재하지 않습니다.", 404, LocalDateTime.now()));
        }

        for (UpdateCartBookOrderRequest cartBookOrderRequest : request) {
            CartBook cartBook = cartBookRepository.findByCart_CartIdAndBook_BookId(cart.getCartId(),
                    cartBookOrderRequest.bookId())
                .orElseThrow(() -> new CartBookException(
                    ErrorStatus.toErrorStatus("장바구니 도서가 존재하지 않습니다.", 404, LocalDateTime.now())
                ));

            if (Objects.isNull(cartBook)) {
                throw new CartBookException(
                    ErrorStatus.toErrorStatus("장바구니에 도서가 존재하지 않습니다.", 404, LocalDateTime.now()));

            }

            if (cartBook.getBookQuantity() - cartBookOrderRequest.quantity() < 1) {
                cartBookRepository.delete(cartBook);
            } else {
                cartBook.updateCartBookQuantity(
                    cartBook.getBookQuantity() - cartBookOrderRequest.quantity());
                cartBookRepository.save(cartBook);
            }
        }
    }
}