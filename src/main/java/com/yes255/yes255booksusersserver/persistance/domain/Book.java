package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "book_isbn", nullable = false, unique = true)
    private String bookIsbn;

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Column(name = "book_description", nullable = false)
    private String bookDescription;

    @Column(name = "book_index")
    private String bookIndex;

    @Column(name = "book_publisher")
    private String bookPublisher;

    @Column(name = "book_publish_date")
    private Date bookPublishDate;

    @Column(name = "book_price", nullable = false)
    private BigDecimal bookPrice;

    @Column(name = "book_selling_price", nullable = false)
    private BigDecimal bookSellingPrice;

    @Column(name = "book_image")
    private String bookImage;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "review_count", nullable = false)
    @ColumnDefault("0")
    private Integer reviewCount;

    @ColumnDefault("0")
    @Column(name = "hits_count", nullable = false)
    private Integer hitsCount;

    @ColumnDefault("0")
    @Column(name = "search_count", nullable = false)
    private Integer searchCount;

    @ColumnDefault("false")
    @Column(name = "book_is_packable")
    private boolean bookIsPackable;

    @Builder
    public Book(Long bookId, String bookIsbn, String bookName, String bookDescription,
                String bookIndex, String bookPublisher, Date bookPublishDate,
                BigDecimal bookPrice, BigDecimal bookSellingPrice, String bookImage,
                Integer quantity, Integer reviewCount, Integer hitsCount, Integer searchCount, boolean bookIsPackable) {
        this.bookId = bookId;
        this.bookIsbn = bookIsbn;
        this.bookName = bookName;
        this.bookDescription = bookDescription;
        this.bookIndex = bookIndex;
        this.bookPublisher = bookPublisher;
        this.bookPublishDate = bookPublishDate;
        this.bookPrice = bookPrice;
        this.bookSellingPrice = bookSellingPrice;
        this.bookImage = bookImage;
        this.quantity = quantity;
        this.reviewCount = reviewCount;
        this.hitsCount = hitsCount;
        this.searchCount = searchCount;
        this.bookIsPackable = bookIsPackable;
    }

    public void updateAll(Book book) {
        this.bookIsbn = Optional.ofNullable(book.getBookIsbn()).orElse(this.bookIsbn);
        this.bookName = Optional.ofNullable(book.getBookName()).orElse(this.bookName);
        this.bookDescription = Optional.ofNullable(book.getBookDescription()).orElse(this.bookDescription);
        this.bookIndex = Optional.ofNullable(book.getBookIndex()).orElse(this.bookIndex);
        this.bookPublisher = Optional.ofNullable(book.getBookPublisher()).orElse(this.bookPublisher);
        this.bookPublishDate = Optional.ofNullable(book.getBookPublishDate()).orElse(this.bookPublishDate);
        this.bookPrice = Optional.ofNullable(book.getBookPrice()).orElse(this.bookPrice);
        this.bookSellingPrice = Optional.ofNullable(book.getBookSellingPrice()).orElse(this.bookSellingPrice);
        this.bookImage = Optional.ofNullable(book.getBookImage()).orElse(this.bookImage);
        this.quantity = Optional.ofNullable(book.getQuantity()).orElse(this.quantity);
        this.reviewCount = Optional.ofNullable(book.getReviewCount()).orElse(this.reviewCount);
        this.hitsCount = Optional.ofNullable(book.getHitsCount()).orElse(this.hitsCount);
        this.searchCount = Optional.ofNullable(book.getSearchCount()).orElse(this.searchCount);
        this.bookIsPackable = Optional.of(book.isBookIsPackable()).orElse(this.bookIsPackable);
    }
}