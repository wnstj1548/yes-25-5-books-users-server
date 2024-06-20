package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Book;

public record CartBookResponse(Book book, int cartBookQuantity) {
}
