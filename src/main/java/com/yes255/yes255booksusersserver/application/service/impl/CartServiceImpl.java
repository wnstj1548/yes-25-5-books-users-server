package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CartService;
import com.yes255.yes255booksusersserver.common.exception.CartNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Cart;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final JpaCartRepository cartRepository;
    private final JpaCartBookRepository cartBookRepository;
    private final JpaUserRepository userRepository;

    // 카트 삭제
    @Override
    public void deleteByUserId(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CartNotFoundException(ErrorStatus.toErrorStatus("유저가 존재하지 않습니다.", 400, LocalDateTime.now())));

        Cart cart = cartRepository.findByUser_UserId(userId);

        cartBookRepository.deleteByCart(cart);
        cartRepository.deleteById(cart.getCartId());
    }
}
