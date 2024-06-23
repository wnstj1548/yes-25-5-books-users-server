package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.Cart;
import com.yes255.yes255booksusersserver.persistance.domain.CartBook;
import com.yes255.yes255booksusersserver.presentation.dto.response.CartBookResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCartBookRepository extends JpaRepository<CartBook, Long> {

    CartBook findByCartBookIdAndCart_CartId(Long cartBookId, Long cartId);

    CartBook findByCart_CartIdAndBook_BookId(Long cartId, Long bookId);

    List<CartBook> findByCart_CartIdOrderByCartBookCreatedAtDesc(Long cartId);

//    List<CartBook> findByBook(Book book);

    void deleteByCart(Cart cart);

    void deleteByCartUserUserId(Long userId);
}
