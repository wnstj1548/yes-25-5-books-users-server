package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookAuthorRepository extends JpaRepository<BookAuthor, Long> {
}
