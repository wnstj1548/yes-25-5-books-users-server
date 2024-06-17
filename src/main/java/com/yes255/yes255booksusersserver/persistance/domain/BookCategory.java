package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "book_category")
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_category_id")
    private Long bookCategoryId;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Category category;

    @Builder
    public BookCategory(Long bookCategoryId, Book book, Category category) {
        this.bookCategoryId = bookCategoryId;
        this.book = book;
        this.category = category;
    }
}
