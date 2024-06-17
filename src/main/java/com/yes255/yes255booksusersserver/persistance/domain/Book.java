package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.Date;

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

    @Column(name = "book_author")
    private String bookAuthor;

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

    @Builder
    public Book(Long bookId, String bookIsbn, String bookName, String bookDescription,
                String bookIndex, String bookAuthor, String bookPublisher, Date bookPublishDate,
                BigDecimal bookPrice, BigDecimal bookSellingPrice, String bookImage,
                Integer quantity, Integer reviewCount, Integer hitsCount, Integer searchCount) {
        this.bookId = bookId;
        this.bookIsbn = bookIsbn;
        this.bookName = bookName;
        this.bookDescription = bookDescription;
        this.bookIndex = bookIndex;
        this.bookAuthor = bookAuthor;
        this.bookPublisher = bookPublisher;
        this.bookPublishDate = bookPublishDate;
        this.bookPrice = bookPrice;
        this.bookSellingPrice = bookSellingPrice;
        this.bookImage = bookImage;
        this.quantity = quantity;
        this.reviewCount = reviewCount;
        this.hitsCount = hitsCount;
        this.searchCount = searchCount;
    }
}
