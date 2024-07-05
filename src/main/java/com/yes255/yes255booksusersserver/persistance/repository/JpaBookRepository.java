package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaBookRepository extends JpaRepository<Book, Long> {
    List<Book> findByBookNameContainingIgnoreCaseAndBookIsDeletedFalse(String bookName);
    Page<Book> findByBookIsDeletedFalse(Pageable pageable);
    List<Book> findByBookIsDeletedFalse();
    List<Book> findByBookIsDeletedTrue();
}
