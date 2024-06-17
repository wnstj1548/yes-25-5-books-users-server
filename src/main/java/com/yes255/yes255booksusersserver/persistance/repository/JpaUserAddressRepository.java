package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserAddressRepository extends JpaRepository <UserAddress, Long> {
}
