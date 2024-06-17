package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCategoryRepository extends JpaRepository<Category, Long> {
}
