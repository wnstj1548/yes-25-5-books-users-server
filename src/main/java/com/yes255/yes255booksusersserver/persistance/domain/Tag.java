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
@Table(name = "tag")
@EntityListeners(AuditingEntityListener.class)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "tag_name")
    private String tagName;

    @LastModifiedDate
    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Builder
    public Tag(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.lastModified = LocalDateTime.now();
    }
}
