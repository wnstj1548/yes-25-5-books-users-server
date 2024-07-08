package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaUserAddressRepository extends JpaRepository <UserAddress, Long> {

    void deleteByUserUserId(Long userId);

    UserAddress findByUserAddressIdAndUserUserId(Long userAddressId, Long userId);

    Page<UserAddress> findByUserUserId(Long userId, Pageable pageable);
}
