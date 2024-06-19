package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CartBookService;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartBookRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateCartBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartBookServiceImpl implements CartBookService {

    private final JpaCartBookRepository cartBookRepository;
    private final JpaBookRepository bookRepository;

    // 장바구니에 도서 추가
    @Override
    public CreateCartBookResponse createCartBookByUserId(Long userId, CreateCartBookRequest request) {

        Book book = bookRepository.findById(userId).orElse(null);

        return null;
    }


    // 장바구니에 도서 수정
    @Override
    public UpdateCartBookResponse updateCartBookByUserId(Long userId, UpdateCartBookRequest request) {
        return null;
    }
}
