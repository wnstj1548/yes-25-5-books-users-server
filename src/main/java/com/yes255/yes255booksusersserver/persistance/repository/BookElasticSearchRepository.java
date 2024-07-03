package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface BookElasticSearchRepository extends ElasticsearchRepository<BookIndex, Long> {

    List<BookIndex> findByBookNameContainsIgnoreCase(String bookName);
    List<BookIndex> findByBookDescriptionContainingIgnoreCase(String bookDescription);

}
