package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.CartBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCartBookRepository extends JpaRepository<CartBook, Long> {
}
