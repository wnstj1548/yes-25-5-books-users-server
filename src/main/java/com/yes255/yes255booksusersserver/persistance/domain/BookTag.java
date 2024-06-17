package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "book_tag")
public class BookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_tag_id")
    private Long bookTagId;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Tag tag;

    @Builder
    public BookTag(Long bookTagId, Book book, Tag tag) {
        this.bookTagId = bookTagId;
        this.book = book;
        this.tag = tag;
    }
}
