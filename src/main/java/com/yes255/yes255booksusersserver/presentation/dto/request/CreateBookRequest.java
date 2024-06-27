
package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;

public record CreateBookRequest(
        @NotNull(message = "ISBN이 없습니다.")
        @Size(min = 10, max = 13)
        String bookIsbn,

        @NotBlank(message = "책 제목은 필수 입력항목입니다.")
        String bookName,

        String bookDescription,

        String index,

        String bookAuthor,

        String bookPublisher,

        @PastOrPresent(message = "출판 일자는 미래일 수 없습니다.")
        Date bookPublishDate,

        @NotNull(message = "책 가격은 필수 입력 항목입니다.")
        BigDecimal bookPrice,

        @NotNull(message = "책 판매 가격은 필수 입력 항목입니다.")
        BigDecimal bookSellingPrice,

        String bookImage,

        @NotNull(message = "수량은 필수 입력 항목입니다.")
        Integer quantity
)
{
    public Book toEntity() {
        return Book.builder()
                .bookId(null)
                .bookIsbn(bookIsbn)
                .bookName(bookName)
                .bookDescription(bookDescription)
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
