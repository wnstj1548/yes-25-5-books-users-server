package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaAuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByAuthorName(String authorName);

    List<Author> findByLastModifiedAfter(LocalDateTime lastModified);

}
