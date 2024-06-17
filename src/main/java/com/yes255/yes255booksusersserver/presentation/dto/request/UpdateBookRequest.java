package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Book;

import java.math.BigDecimal;
import java.util.Date;

public record UpdateBookRequest(long bookId, String bookIsbn, String bookName, String bookDescription,
                                String bookAuthor, String bookPublisher, Date bookPublishDate,
                                BigDecimal bookPrice, BigDecimal bookSellingPrice, String bookImage,
                                Integer quantity, Integer reviewCount, Integer hitsCount, Integer searchCount) {
    public Book toEntity() {
        return Book.builder()
                .bookId(bookId)
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
                .reviewCount(reviewCount)
                .hitsCount(hitsCount)
                .searchCount(searchCount)
                .build();
    }
}
