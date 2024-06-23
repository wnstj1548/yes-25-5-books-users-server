package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateCartBookResponse;

import java.util.List;

public interface CartBookService {

    CreateCartBookResponse createCartBookByUserId(Long userId, CreateCartBookRequest request);

    UpdateCartBookResponse updateCartBookByUserId(Long userId, UpdateCartBookRequest request);

    void deleteCartBookByUserIdByCartBookId(Long userId, Long cartBookId);

    List<CartBookResponse> findAllCartBookById(Long userId);

//    void deleteAllCartBookByCartId(Long cartId);
}
