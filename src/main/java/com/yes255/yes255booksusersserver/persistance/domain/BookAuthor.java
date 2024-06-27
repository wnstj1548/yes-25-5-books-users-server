package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "book_author")
public class BookAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_author_id")
    private Long bookAuthorId;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Author author;

    @Builder
    public BookAuthor(Long bookAuthorId, Book book, Author author) {
        this.bookAuthorId = bookAuthorId;
        this.book = book;
        this.author = author;
    }
}
