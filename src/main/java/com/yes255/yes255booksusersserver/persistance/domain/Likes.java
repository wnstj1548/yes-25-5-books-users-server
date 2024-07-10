package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "likes")
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")
    private Long likesId;

    @Column(name = "likes_status")
    private boolean likesStatus;

    @ManyToOne
    private Book book;

    @ManyToOne
    private User user;

    @Builder
    public Likes(Long likesId, boolean likesStatus, Book book, User user) {
        this.likesId = likesId;
        this.likesStatus = likesStatus;
        this.book = book;
        this.user = user;
    }
}
