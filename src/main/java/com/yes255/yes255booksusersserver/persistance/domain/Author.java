package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "author")
@EntityListeners(AuditingEntityListener.class)
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id",nullable = false)
    private Long authorId;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @LastModifiedDate
    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Builder
    public Author(Long authorId, String authorName) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.lastModified = LocalDateTime.now();
    }
}
