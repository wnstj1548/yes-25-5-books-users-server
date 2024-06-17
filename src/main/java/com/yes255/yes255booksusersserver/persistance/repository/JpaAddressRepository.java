package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JpaAddressRepository extends JpaRepository<Address, Long>{
}
