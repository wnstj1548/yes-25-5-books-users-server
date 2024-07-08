package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.index.TagIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface TagElasticSearchRepository extends ElasticsearchRepository<TagIndex, String> {

}
