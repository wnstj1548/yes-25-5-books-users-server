package com.yes255.yes255booksusersserver.persistance.domain.index;

import com.yes255.yes255booksusersserver.persistance.domain.Tag;
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
@Document(indexName = "yes255_tags")
public class TagIndex {

    @Id
    @Field(name = "tag_id", type = FieldType.Keyword)
    private String tagId;

    @Field(name = "tag_name", type = FieldType.Text)
    private String tagName;

    public static TagIndex fromTag(Tag tag) {
        return TagIndex.builder()
                .tagId(tag.getTagId().toString())
                .tagName(tag.getTagName())
                .build();
    }
}
