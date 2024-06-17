package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
