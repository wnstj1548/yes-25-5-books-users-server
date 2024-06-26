package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAuthorRepository extends JpaRepository<Author, Long> {
}
