package com.yes255.yes255booksusersserver.persistance.domain.index;

import com.yes255.yes255booksusersserver.persistance.domain.Category;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Document(indexName = "yes255_category")
public class CategoryIndex {

    @Id
    @Field(name = "category_id", type = FieldType.Keyword)
    private String categoryId;

    @Field(name = "category_name", type = FieldType.Text)
    private String categoryName;

    public static CategoryIndex fromCategory(Category category) {
        return CategoryIndex.builder()
                .categoryId(category.getCategoryId().toString())
                .categoryName(category.getCategoryName())
                .build();
    }
}