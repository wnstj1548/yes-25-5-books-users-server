package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookOrderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;

import java.util.List;

public interface CartBookService {

    CreateCartBookResponse createCartBookByUserId(Long userId, CreateCartBookRequest request);

    UpdateCartBookResponse updateCartBookByUserId(Long userId, UpdateCartBookRequest request);

    void deleteCartBookByUserIdByCartBookId(Long userId, Long cartBookId);

    List<CartBookResponse> findAllCartBookById(Long userId);

    void updateCartBookOrderByUserId(Long userId, List<UpdateCartBookOrderRequest> request);

//    void deleteAllCartBookByCartId(Long cartId);
}
