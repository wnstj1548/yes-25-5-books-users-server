package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCartRepository extends JpaRepository<Cart, Long> {

    Cart findByCustomer_UserId(Long customerId);

    void deleteByCustomer_UserId(Long customerId);
}
