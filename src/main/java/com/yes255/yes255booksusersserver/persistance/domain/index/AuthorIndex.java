package com.yes255.yes255booksusersserver.persistance.domain.index;

import com.yes255.yes255booksusersserver.persistance.domain.Author;
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
@Document(indexName = "yes255_author")
public class AuthorIndex {

    @Id
    @Field(name = "author_id", type = FieldType.Keyword)
    private String authorId;

    @Field(name = "author_name", type = FieldType.Text)
    private String authorName;

    public static AuthorIndex fromAuthor(Author author) {
        return AuthorIndex.builder()
                .authorId(author.getAuthorId().toString())
                .authorName(author.getAuthorName())
                .build();
    }
}
