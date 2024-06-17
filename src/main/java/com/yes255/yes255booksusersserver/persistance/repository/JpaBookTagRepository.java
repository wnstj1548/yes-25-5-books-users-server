package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookTagRepository extends JpaRepository<BookTag, Long> {
}
