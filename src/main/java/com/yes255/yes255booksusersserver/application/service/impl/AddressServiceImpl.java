package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.AddressService;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.AddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

    private final JpaAddressRepository addressRepository;

    @Override
    public CreateAddressResponse createAddress(CreateAddressRequest addressRequest) {

        Address address = addressRepository.save(Address.builder()
                        .addressZip(addressRequest.addressZip())
                        .addressRaw(addressRequest.addressRaw())
                        .build());

        return CreateAddressResponse.builder()
                .addressZip(address.getAddressZip())
                .addressRaw(address.getAddressRaw())
                .build();
    }

    @Override
    public UpdateAddressResponse updateAddress(Long addressId, UpdateAddressRequest addressRequest) {

        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // 기존 주소 정보를 기반으로 새로운 주소 객체 생성
        Address updatedAddress = Address.builder()
                .addressId(existingAddress.getAddressId())
                .addressZip(addressRequest.addressZip() != null ? addressRequest.addressZip() : existingAddress.getAddressZip())
                .addressRaw(addressRequest.addressRaw() != null ? addressRequest.addressRaw() : existingAddress.getAddressRaw())
                .build();
        addressRepository.save(updatedAddress);

        return UpdateAddressResponse.builder()
                .addressZip(updatedAddress.getAddressZip())
                .addressRaw(updatedAddress.getAddressRaw())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public AddressResponse findAddressById(Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        return AddressResponse.builder()
                .addressZip(address.getAddressZip())
                .addressRaw(address.getAddressRaw())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<AddressResponse> findAllAddresses() {

        List<Address> addresses = addressRepository.findAll();

        return addresses.stream()
                .map(address -> AddressResponse.builder()
                        .addressId(address.getAddressId())
                        .addressZip(address.getAddressZip())
                        .addressRaw(address.getAddressRaw())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAddress(Long addressId) {

        addressRepository.deleteById(addressId);
    }
}
