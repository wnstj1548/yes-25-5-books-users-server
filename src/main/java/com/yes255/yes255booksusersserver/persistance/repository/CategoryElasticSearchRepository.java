package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.index.CategoryIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryElasticSearchRepository extends ElasticsearchRepository<CategoryIndex, String> {
}
