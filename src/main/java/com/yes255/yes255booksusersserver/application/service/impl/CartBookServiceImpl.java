package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CartBookService;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.CartBookException;
import com.yes255.yes255booksusersserver.common.exception.CartException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.DeleteCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookOrderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartBookServiceImpl implements CartBookService {

    private final JpaCartRepository cartRepository;
    private final JpaCartBookRepository cartBookRepository;
    private final JpaBookRepository bookRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    // 장바구니에 도서 추가
    @Override
    public CreateCartBookResponse createCartBookByUserId(String cartId,
        CreateCartBookRequest request) {

        Book book = bookRepository.findById(request.bookId())
            .orElseThrow(() -> new BookNotFoundException(
                ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 404, LocalDateTime.now())));

        if (!isAvailableAddCart(book, request.quantity())) {
            throw new CartException(
                ErrorStatus.toErrorStatus("장바구니 추가가 불가능한 도서입니다.", 403, LocalDateTime.now()));
        }

        Map<Long, Integer> cart = getOrCrateCart(cartId);

        if (cart.containsKey(request.bookId())) {
            throw new CartBookException(
                ErrorStatus.toErrorStatus("동일한 도서가 이미 장바구니에 존재합니다.", 409, LocalDateTime.now()));
        }

        cart.put(request.bookId(), cart.getOrDefault(request.bookId(), 0) + request.quantity());
        updateCartInRedis(cartId, cart);

        return CreateCartBookResponse.from(cartId);
    }

    private void updateCartInRedis(String cartId, Map<Long, Integer> cart) {
        redisTemplate.opsForHash().put("cart", cartId, cart);
    }

    private Map<Long, Integer> getOrCrateCart(String cartId) {
        Object cartObject = redisTemplate.opsForHash().get("cart", cartId);
        Map<Long, Integer> cart;

        if (Objects.isNull(cartObject)) {
            cart = new HashMap<>();
            redisTemplate.opsForHash().put("cart", cartId, cart);
        } else {
            cart = (Map<Long, Integer>) cartObject;
        }

        return cart;
    }

    // 장바구니에 도서 수정 (수량 조절)
    @Override
    public UpdateCartBookResponse updateCartBookByUserId(String cartId,
        Long bookId, UpdateCartBookRequest request) {

        Object cartObject = redisTemplate.opsForHash().get("cart", cartId);
        if (Objects.isNull(cartObject)) {
            throw new CartException(
                ErrorStatus.toErrorStatus("장바구니를 찾을 수 없습니다.", 404, LocalDateTime.now()));
        }

        Map<Long, Integer> cart = convertToLongKeyMap((Map<String, Integer>) cartObject);
        if (cart.containsKey(bookId)) {
            if (cart.get(bookId) > 0) {
                cart.put(bookId, request.quantity());
            }
        } else {
            throw new CartBookException(
                ErrorStatus.toErrorStatus("장바구니에 책이 존재하지 않습니다.", 404, LocalDateTime.now()));
        }

        updateCartInRedis(cartId, cart);

        return UpdateCartBookResponse.of(cartId, request.quantity());
    }

    // 장바구니에서 도서 삭제
    @Override
    public DeleteCartBookResponse deleteCartBookByUserIdByCartBookId(String cartId, Long bookId) {
        Object cartObject = redisTemplate.opsForHash().get("cart", cartId);
        if (Objects.isNull(cartObject)) {
            throw new CartException(
                ErrorStatus.toErrorStatus("카트를 찾을 수 없습니다.", 404, LocalDateTime.now()));
        }

        Map<Long, Integer> cart = convertToLongKeyMap((Map<String, Integer>) cartObject);
        if (cart.containsKey(bookId)) {
            cart.remove(bookId);
        } else {
            throw new CartBookException(
                ErrorStatus.toErrorStatus("장바구니에 책이 존재하지 않습니다.", 404, LocalDateTime.now()));
        }

        updateCartInRedis(cartId, cart);

        return DeleteCartBookResponse.from(bookId);
    }

    // 장바구니 도서 목록 조회
    @Override
    public List<CartBookResponse> findAllCartBookById(String cartId) {
        Object cartObject = redisTemplate.opsForHash().get("cart", cartId);
        if (Objects.isNull(cartObject)) {
            return Collections.emptyList();
        }

        Map<Long, Integer> cart = convertToLongKeyMap((Map<String, Integer>) cartObject);
        List<CartBookResponse> cartBookResponses = new ArrayList<>();

        for (Entry<Long, Integer> entry : cart.entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();

            Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(
                    ErrorStatus.toErrorStatus("도서를 찾을 수 없습니다.", 404, LocalDateTime.now())));

            CartBookResponse cartBookResponse = CartBookResponse.of(book, quantity);
            cartBookResponses.add(cartBookResponse);
        }

        return cartBookResponses;
    }

    private Map<Long, Integer> convertToLongKeyMap(Map<String, Integer> map) {
        Map<Long, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            result.put(Long.parseLong(entry.getKey()), entry.getValue());
        }
        return result;
    }

    @Override
    public void updateCartBookOrderByUserId(List<UpdateCartBookOrderRequest> request) {
        for (UpdateCartBookOrderRequest updateCartBookOrderRequest : request) {
            String cartId = updateCartBookOrderRequest.cartId();
            Long bookId = updateCartBookOrderRequest.bookId();
            int quantityToDecrease = updateCartBookOrderRequest.quantity();

            Object cartObject = redisTemplate.opsForHash().get("cart", cartId);
            if (Objects.isNull(cartObject)) {
                throw new CartException(
                    ErrorStatus.toErrorStatus("장바구니를 찾을 수 없습니다.", 404, LocalDateTime.now()));
            }

            Map<Long, Integer> cart = convertToLongKeyMap((Map<String, Integer>) cartObject);
            if (cart.containsKey(bookId)) {
                int currentQuantity = cart.get(bookId);

                if (currentQuantity >= quantityToDecrease) {
                    cart.put(bookId, currentQuantity - quantityToDecrease);
                    if (cart.get(bookId) == 0) {
                        cart.remove(bookId);
                    }
                } else {
                    throw new CartBookException(
                        ErrorStatus.toErrorStatus("장바구니에 충분한 수량이 없습니다.", 400, LocalDateTime.now()));
                }
            } else {
                throw new CartBookException(
                    ErrorStatus.toErrorStatus("장바구니에 책이 존재하지 않습니다.", 404, LocalDateTime.now()));
            }
            updateCartInRedis(cartId, cart);
        }

        log.info("장바구니 재고가 성공적으로 감소되었습니다.");
    }

    private boolean isAvailableAddCart(Book book, int quantity) {
        if (quantity > book.getQuantity()) {
            return false;
        }

        if (book.isBookIsDeleted()) {
            return false;
        }

        return true;
    }
}