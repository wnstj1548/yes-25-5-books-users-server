package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.index.AuthorIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface AuthorElasticSearchRepository extends ElasticsearchRepository<AuthorIndex, String> {

}
