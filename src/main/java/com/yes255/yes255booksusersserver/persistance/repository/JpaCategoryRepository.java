package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaCategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByLastModifiedAfter(LocalDateTime lastModified);

}
