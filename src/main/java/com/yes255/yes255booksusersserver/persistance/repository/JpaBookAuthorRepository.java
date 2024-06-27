package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Author;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaBookAuthorRepository extends JpaRepository<BookAuthor, Long> {

    List<BookAuthor> findByAuthor(Author author);
    List<BookAuthor> findByBook(Book book);
}
