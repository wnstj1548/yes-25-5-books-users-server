package com.yes255.yes255booksusersserver.persistance.domain.index;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Document(indexName = "book")
public class BookIndex {

    @Id
    @Field(name = "book_id", type = FieldType.Keyword)
    private String bookId;

    @Field(name = "book_isbn", type = FieldType.Keyword)
    private String bookIsbn;

    @Field(name = "book_name", type = FieldType.Text)
    private String bookName;

    @Field(name = "book_description", type = FieldType.Text)
    private String bookDescription;

    @Field(name = "book_publisher", type = FieldType.Text)
    private String bookPublisher;

    @Field(name = "book_publish_date", type = FieldType.Date, format = DateFormat.basic_date)
    private Date bookPublishDate;

    @Field(name = "book_price", type = FieldType.Double)
    private BigDecimal bookPrice;

    @Field(name = "book_selling_price", type = FieldType.Double)
    private BigDecimal bookSellingPrice;

    @Field(name = "book_image", type = FieldType.Text)
    private String bookImage;

    @Field(name = "quantity", type = FieldType.Integer)
    private Integer quantity;

    @Field(name = "review_count", type = FieldType.Integer)
    private Integer reviewCount;

    @Field(name = "hits_count", type = FieldType.Integer)
    private Integer hitsCount;

    @Field(name = "search_count", type = FieldType.Integer)
    private Integer searchCount;

    @Field(name = "book_is_packable", type = FieldType.Boolean)
    private boolean bookIsPackable;

    @Field(name = "authors", type = FieldType.Nested)
    private List<AuthorIndex> authors;

    @Field(name = "tags", type = FieldType.Nested)
    private List<TagIndex> tags;

    public static BookIndex updateAuthorsAndTags(BookIndex book, List<AuthorIndex> authors, List<TagIndex> tags) {
        return BookIndex.builder()
                .bookIsbn(book.getBookIsbn())
                .bookName(book.getBookName())
                .bookDescription(book.getBookDescription())
                .bookPublisher(book.getBookPublisher())
                .bookPublishDate(book.getBookPublishDate())
                .bookPrice(book.getBookPrice())
                .bookSellingPrice(book.getBookSellingPrice())
                .bookImage(book.getBookImage())
                .quantity(book.getQuantity())
                .reviewCount(book.getReviewCount())
                .hitsCount(book.getHitsCount())
                .searchCount(book.getSearchCount())
                .bookIsPackable(book.isBookIsPackable())
                .authors(authors)
                .tags(tags)
                .build();
    }

    public static BookIndex fromBook(Book book) {
        return BookIndex.builder()
                .bookId(book.getBookId().toString())
                .bookIsbn(book.getBookIsbn())
                .bookName(book.getBookName())
                .bookDescription(book.getBookDescription())
                .bookPublisher(book.getBookPublisher())
                .bookPublishDate(book.getBookPublishDate())
                .bookPrice(book.getBookPrice())
                .bookSellingPrice(book.getBookSellingPrice())
                .bookImage(book.getBookImage())
                .quantity(book.getQuantity())
                .reviewCount(book.getReviewCount())
                .hitsCount(book.getHitsCount())
                .searchCount(book.getSearchCount())
                .bookIsPackable(book.isBookIsPackable())
                .build();
    }
}
