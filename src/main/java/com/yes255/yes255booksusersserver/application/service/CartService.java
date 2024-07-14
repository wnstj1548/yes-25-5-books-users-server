package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.CreateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.DeleteCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookOrderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.cartbook.UpdateCartBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.CreateCartBookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.cartbook.UpdateCartBookResponse;
import java.util.List;

public interface CartService {

    CreateCartBookResponse createCartBookByCartId(String cartId, CreateCartBookRequest createCartBookRequest);

    UpdateCartBookResponse updateCartBookByUserId(String cartId, Long bookId, UpdateCartBookRequest updateCartBookRequest);

    DeleteCartBookResponse deleteCartBookByUserIdByCartBookId(String cartId, Long bookId);

    List<CartBookResponse> findAllCartBookById(String cartId);

    void updateCartBookOrderByUserId(List<UpdateCartBookOrderRequest> cartBookRequest);
}
