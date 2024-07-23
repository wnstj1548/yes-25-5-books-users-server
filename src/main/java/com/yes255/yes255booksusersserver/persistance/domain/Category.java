package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "category")
@NamedEntityGraph(
        name = "Category.withSubCategories",
        attributeNodes = @NamedAttributeNode("subCategories")
)
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @LastModifiedDate
    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> subCategories;

    @Builder
    public Category(Long categoryId, String categoryName, Category parentCategory, List<Category> subCategories) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
        this.subCategories = subCategories;
        this.lastModified = LocalDateTime.now();
    }
}
