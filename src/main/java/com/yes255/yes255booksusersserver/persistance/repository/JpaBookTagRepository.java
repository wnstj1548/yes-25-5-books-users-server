package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaBookTagRepository extends JpaRepository<BookTag, Long> {

    List<BookTag> findByBook(Book book);
    List<BookTag> findByTag(Tag tag);
}
