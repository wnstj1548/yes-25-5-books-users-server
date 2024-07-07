package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BookIndexResponse(
        String bookId,
        String bookIsbn,
        String bookName,
        String bookDescription,
        String bookPublisher,
        BigDecimal bookPrice,
        BigDecimal bookSellingPrice,
        String bookImage,
        Integer quantity,
        Integer reviewCount,
        Integer hitsCount,
        Integer searchCount,
        boolean bookIsPackable,
        List<String> authors,
        List<String> tags
)
{
    public static BookIndexResponse fromIndex(BookIndex bookIndex) {

        return BookIndexResponse.builder()
                .bookId(bookIndex.getBookId())
                .bookDescription(bookIndex.getBookDescription())
                .bookName(bookIndex.getBookName())
                .bookIsbn(bookIndex.getBookIsbn())
                .bookPublisher(bookIndex.getBookPublisher())
                .bookPrice(bookIndex.getBookPrice())
                .bookSellingPrice(bookIndex.getBookSellingPrice())
                .bookImage(bookIndex.getBookImage())
                .quantity(bookIndex.getQuantity())
                .reviewCount(bookIndex.getReviewCount())
                .hitsCount(bookIndex.getHitsCount())
                .searchCount(bookIndex.getSearchCount())
                .bookIsPackable(bookIndex.isBookIsPackable())
                .authors(bookIndex.getAuthors())
                .tags(bookIndex.getTags())
                .build();
    }
}
