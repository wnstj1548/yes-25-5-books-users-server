package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Author;
import com.yes255.yes255booksusersserver.persistance.domain.index.AuthorIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface AuthorElasticSearchRepository extends ElasticsearchRepository<AuthorIndex, Long> {

    List<AuthorIndex> findByAuthorName(String authorName);
}
