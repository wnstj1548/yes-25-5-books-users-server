package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Address;

import java.util.List;

public interface AddressService {
    Address createAddress(Address address);
    Address updateAddress(Long addressId, Address address);
    Address getAddressById(Long addressId);
    List<Address> getAllAddresses();
    void deleteAddress(Long addressId);
}
