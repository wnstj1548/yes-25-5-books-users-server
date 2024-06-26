package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.AddressService;
import com.yes255.yes255booksusersserver.common.exception.AddressException;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.AddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.CreateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.UpdateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.AddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.CreateAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.UpdateAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

    private final JpaAddressRepository addressRepository;

    @Override
    public CreateAddressResponse createAddress(CreateAddressRequest addressRequest) {

        Address checkAddress = addressRepository.findAddressByAddressRawOrAddressZip(addressRequest.addressRaw(), addressRequest.addressZip());

        if (checkAddress != null) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("이미 주소가 존재합니다.", 400, LocalDateTime.now()));
        }

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
                .orElseThrow(() -> new AddressException(ErrorStatus.toErrorStatus("주소가 존재하지 않습니다.", 400, LocalDateTime.now())));

        // 기존 주소 정보를 기반으로 새로운 주소 객체 생성
        Address updatedAddress = Address.builder()
                .addressId(existingAddress.getAddressId())
                .addressZip(addressRequest.addressZip() != null ? addressRequest.addressZip() : existingAddress.getAddressZip())
                .addressRaw(addressRequest.addressRaw() != null ? addressRequest.addressRaw() : existingAddress.getAddressRaw())
                .build();
        addressRepository.save(updatedAddress);

        return UpdateAddressResponse.builder()
                .addressId(updatedAddress.getAddressId())
                .addressZip(updatedAddress.getAddressZip())
                .addressRaw(updatedAddress.getAddressRaw())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public AddressResponse findAddressById(Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressException(ErrorStatus.toErrorStatus("주소가 존재하지 않습니다.", 400, LocalDateTime.now())));

        return AddressResponse.builder()
                .addressId(addressId)
                .addressZip(address.getAddressZip())
                .addressRaw(address.getAddressRaw())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<AddressResponse> findAllAddresses() {

        List<Address> addresses = addressRepository.findAll();

        if (addresses.isEmpty()) {
            throw new AddressException(ErrorStatus.toErrorStatus("주소가 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

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

    // 주소 찾기 (우편번호나 주소를 통해)
    @Transactional(readOnly = true)
    @Override
    public AddressResponse findByAddressZipOrAddressRaw(AddressRequest addressRequest) {

        Address address = addressRepository.findAddressByAddressRawOrAddressZip(addressRequest.addressRaw(), addressRequest.addressZip());

        if (Objects.isNull(address)) {
            throw new AddressException(ErrorStatus.toErrorStatus("주소가 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .addressZip(address.getAddressZip())
                .addressRaw(address.getAddressRaw())
                .build();
    }
}
