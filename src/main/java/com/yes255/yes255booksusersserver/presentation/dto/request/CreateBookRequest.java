package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Book;

import java.math.BigDecimal;
import java.util.Date;

public record CreateBookRequest(String bookIsbn, String bookName, String bookDescription,
                                String bookAuthor, String bookPublisher, Date bookPublishDate,
                                BigDecimal bookPrice, BigDecimal bookSellingPrice, String bookImage,
                                Integer quantity) {
    public Book toEntity() {
        return Book.builder()
                .bookId(null)
                .bookIsbn(bookIsbn)
                .bookName(bookName)
                .bookDescription(bookDescription)
                .bookAuthor(bookAuthor)
                .bookPublisher(bookPublisher)
                .bookPublishDate(bookPublishDate)
                .bookPrice(bookPrice)
                .bookSellingPrice(bookSellingPrice)
                .bookImage(bookImage)
                .quantity(quantity)
                .reviewCount(0)
                .hitsCount(0)
                .searchCount(0)
                .build();
    }
}
