package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "book_description", nullable = false, columnDefinition = "TEXT")
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

    @ColumnDefault("false")
    @Column(name = "book_is_deleted")
    private boolean bookIsDeleted;

    @OneToMany(mappedBy = "book", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Book(Long bookId, String bookIsbn, String bookName, String bookDescription,
                String bookIndex, String bookPublisher, Date bookPublishDate,
                BigDecimal bookPrice, BigDecimal bookSellingPrice, String bookImage,
                Integer quantity, Integer reviewCount, Integer hitsCount, Integer searchCount,
                boolean bookIsPackable, boolean bookIsDeleted) {
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
        this.bookIsDeleted = bookIsDeleted;
    }

    public void updateAll(Book book) {
        updateIsbn(book.getBookIsbn());
        updateBookName(book.getBookName());
        updateBookDescription(book.getBookDescription());
        updateBookPublisher(book.getBookPublisher());
        updateBookPublishDate(book.getBookPublishDate());
        updateBookPrice(book.getBookPrice());
        updateBookSellingPrice(book.getBookSellingPrice());
        updateBookImage(book.getBookImage());
        updateQuantity(book.getQuantity());
        updateBookIsPackable(book.isBookIsPackable());
    }

    public void updateIsbn(String isbn) {
        this.bookIsbn = isbn;
    }

    public void updateBookName(String bookName) {
        this.bookName = bookName;
    }

    public void updateBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public void updateBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public void updateBookPublishDate(Date bookPublishDate) {
        this.bookPublishDate = bookPublishDate;
    }

    public void updateBookPrice(BigDecimal bookPrice) {
        this.bookPrice = bookPrice;
    }

    public void updateBookSellingPrice(BigDecimal bookSellingPrice) {
        this.bookSellingPrice = bookSellingPrice;
    }

    public void updateBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void updateBookIsPackable(boolean bookIsPackable) {
        this.bookIsPackable = bookIsPackable;
    }

    public void delete() {
        this.bookIsDeleted = true;
    }

    public void updateBookHitsCount(Integer bookHitsCount) { this.hitsCount = bookHitsCount; }

    public void updateBookIsDeleted(boolean bookIsDeleted) { this.bookIsDeleted = bookIsDeleted; }
}