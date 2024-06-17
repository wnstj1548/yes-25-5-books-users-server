package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookCategoryRepository extends JpaRepository<BookCategory, Long> {
}
