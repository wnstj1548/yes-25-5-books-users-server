package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaUserAddressRepository extends JpaRepository <UserAddress, Long> {

    void deleteByUserUserId(Long userId);

    List<UserAddress> findByUserUserId(Long userId);
}
