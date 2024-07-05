package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.Cart;
import com.yes255.yes255booksusersserver.persistance.domain.CartBook;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCartBookRepository extends JpaRepository<CartBook, Long> {

    CartBook findByCartBookIdAndCart_CartId(Long cartBookId, Long cartId);
    Boolean existsByBookAndCart(Book book, Cart cart);

    Optional<CartBook> findByCart_CartIdAndBook_BookId(Long cartId, Long bookId);

    List<CartBook> findByCart_CartIdOrderByCartBookCreatedAtDesc(Long cartId);

    List<CartBook> findByBook(Book book);

    void deleteByCart(Cart cart);

    Optional<CartBook> findByCart(Cart cart);

    void deleteByCartAndBook_BookId(Cart cart, Long bookId);
}
