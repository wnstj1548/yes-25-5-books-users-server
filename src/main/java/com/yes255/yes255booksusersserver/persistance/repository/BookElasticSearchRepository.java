package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookElasticSearchRepository extends ElasticsearchRepository<BookIndex, String> {

    @Query("{\"bool\": { \"must\": [ {\"wildcard\": {\"book_name\": \"*?0*\"}}]}}")
    Page<BookIndex> findByBookNameContainsIgnoreCase(String keyword, Pageable pageable);

    @Query("{\"bool\": { \"must\": [ {\"wildcard\": {\"book_description\": \"*?0*\"}}]}}")
    Page<BookIndex> findByBookDescriptionContainsIgnoreCase(String bookDescription, Pageable pageable);

    @Query("{\"bool\": { \"must\": [ {\"wildcard\": {\"authors\": \"*?0*\"}}]}}")
    Page<BookIndex> findByAuthorsContainingIgnoreCase(String authorName, Pageable pageable);

    @Query("{\"bool\": { \"must\": [ {\"wildcard\": {\"tags\": \"*?0*\"}}]}}")
    Page<BookIndex> findByTagsContainingIgnoreCase(String tagName, Pageable pageable);

    @Query("{\"bool\": { \"must\": [ {\"wildcard\": {\"categories\": \"*?0*\"}}]}}")
    Page<BookIndex> findByCategoriesContainingIgnoreCase(String categoryName, Pageable pageable);

    @Query("{\"multi_match\": {" +
            "\"query\": \"?0\", " +
            "\"fields\": [" +
            "\"book_name^2\", " +
            "\"book_description^1.5\", " +
            "\"tags\", " +
            "\"authors^1.5\", " +
            "\"categories^2\"" +
            "]" +
            "}}")
    Page<BookIndex> searchAllFields(String query, Pageable pageable);

}
