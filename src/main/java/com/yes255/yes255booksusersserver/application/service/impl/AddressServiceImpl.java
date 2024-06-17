package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.AddressService;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

    private final JpaAddressRepository addressRepository;

    @Override
    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(Long addressId, Address address) {
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // 기존 주소 정보를 기반으로 새로운 주소 객체 생성
        Address updatedAddress = Address.builder()
                .addressId(existingAddress.getAddressId())
                .addressZip(address.getAddressZip() != null ? address.getAddressZip() : existingAddress.getAddressZip())
                .addressRaw(address.getAddressRaw() != null ? address.getAddressRaw() : existingAddress.getAddressRaw())
               // .userAddresses(existingAddress.getAddress())
                .build();

        return addressRepository.save(updatedAddress);
    }

    @Override
    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}
