package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCouponUserRepository extends JpaRepository<CouponUser, Long> {
}
