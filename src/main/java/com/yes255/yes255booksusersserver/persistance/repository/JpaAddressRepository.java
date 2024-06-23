package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.presentation.dto.response.AddressResponse;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JpaAddressRepository extends JpaRepository<Address, Long>{

    Address findAddressByAddressRawOrAddressZip(String addressRaw, String addressZip);
}
