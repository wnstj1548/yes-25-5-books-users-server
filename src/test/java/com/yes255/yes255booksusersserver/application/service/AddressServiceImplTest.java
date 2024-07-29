package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.AddressServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.AddressException;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.AddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.CreateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.UpdateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.AddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.CreateAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.UpdateAddressResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private JpaAddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    @DisplayName("주소 생성 - 성공")
    @Test
    void testCreateAddress_Success() {

        CreateAddressRequest request = CreateAddressRequest.builder()
                .addressZip("12345")
                .addressRaw("Test Address")
                .build();

        when(addressRepository.findAddressByAddressRawOrAddressZip(request.addressRaw(), request.addressZip())).thenReturn(null);

        Address savedAddress = Address.builder()
                .addressId(1L)
                .addressZip(request.addressZip())
                .addressRaw(request.addressRaw())
                .build();

        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);

        CreateAddressResponse response = addressService.createAddress(request);

        assertNotNull(response);
        assertEquals(request.addressZip(), response.addressZip());
        assertEquals(request.addressRaw(), response.addressRaw());
    }

    @DisplayName("주소 생성 - 실패 (이미 존재하는 주소)")
    @Test
    void testCreateAddress_AddressAlreadyExists() {

        CreateAddressRequest request = CreateAddressRequest.builder()
                .addressZip("12345")
                .addressRaw("Test Address")
                .build();

        Address existingAddress = Address.builder()
                .addressId(1L)
                .addressZip(request.addressZip())
                .addressRaw(request.addressRaw())
                .build();

        when(addressRepository.findAddressByAddressRawOrAddressZip(request.addressRaw(), request.addressZip())).thenReturn(existingAddress);

        assertThrows(ApplicationException.class, () -> {
            addressService.createAddress(request);
        });
    }

    @DisplayName("주소 수정 - 성공")
    @Test
    void testUpdateAddress_Success() {

        Long addressId = 1L;
        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .addressZip("54321")
                .addressRaw("Updated Address")
                .build();

        Address existingAddress = Address.builder()
                .addressId(addressId)
                .addressZip("12345")
                .addressRaw("Test Address")
                .build();

        Address updatedAddress = Address.builder()
                .addressId(addressId)
                .addressZip(request.addressZip())
                .addressRaw(request.addressRaw())
                .build();

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(updatedAddress);

        UpdateAddressResponse response = addressService.updateAddress(addressId, request);

        assertNotNull(response);
        assertEquals(addressId, response.addressId());
        assertEquals(request.addressZip(), response.addressZip());
        assertEquals(request.addressRaw(), response.addressRaw());
    }

    @DisplayName("주소 수정 - 실패 (주소가 존재하지 않음)")
    @Test
    void testUpdateAddress_AddressNotFound() {

        Long addressId = 1L;
        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .addressZip("54321")
                .addressRaw("Updated Address")
                .build();

        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(AddressException.class, () -> {
            addressService.updateAddress(addressId, request);
        });
    }

    @DisplayName("특정 주소 조회 - 성공")
    @Test
    void testFindAddressById_Success() {

        Long addressId = 1L;
        Address existingAddress = Address.builder()
                .addressId(addressId)
                .addressZip("12345")
                .addressRaw("Test Address")
                .build();

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));

        AddressResponse response = addressService.findAddressById(addressId);

        assertNotNull(response);
        assertEquals(addressId, response.addressId());
        assertEquals(existingAddress.getAddressZip(), response.addressZip());
        assertEquals(existingAddress.getAddressRaw(), response.addressRaw());
    }

    @DisplayName("특정 주소 조회 - 실패 (주소가 존재하지 않음)")
    @Test
    void testFindAddressById_AddressNotFound() {

        Long addressId = 1L;

        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(AddressException.class, () -> {
            addressService.findAddressById(addressId);
        });
    }

    @DisplayName("모든 주소 조회 - 성공")
    @Test
    void testFindAllAddresses_Success() {

        Address address = Address.builder()
                .addressId(1L)
                .addressZip("12345")
                .addressRaw("Test Address")
                .build();

        List<Address> addresses = Collections.singletonList(address);

        when(addressRepository.findAll()).thenReturn(addresses);

        List<AddressResponse> responses = addressService.findAllAddresses();

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(addresses.size(), responses.size());

        AddressResponse response = responses.getFirst();
        assertEquals(address.getAddressId(), response.addressId());
        assertEquals(address.getAddressZip(), response.addressZip());
        assertEquals(address.getAddressRaw(), response.addressRaw());
    }

    @DisplayName("모든 주소 조회 - 실패 (주소가 존재하지 않음)")
    @Test
    void testFindAllAddresses_AddressNotFound() {

        when(addressRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(AddressException.class, () -> {
            addressService.findAllAddresses();
        });
    }

    @DisplayName("주소 삭제 - 성공")
    @Test
    void testDeleteAddress_Success() {

        Long addressId = 1L;

        assertDoesNotThrow(() -> {
            addressService.deleteAddress(addressId);
        });
    }

    @DisplayName("주소 찾기 (우편번호나 주소를 통해) - 성공")
    @Test
    void testFindByAddressZipOrAddressRaw_Success() {

        AddressRequest request = AddressRequest.builder()
                .addressZip("12345")
                .addressRaw("Test Address")
                .build();

        Address address = Address.builder()
                .addressId(1L)
                .addressZip(request.addressZip())
                .addressRaw(request.addressRaw())
                .build();

        when(addressRepository.findAddressByAddressRawOrAddressZip(request.addressRaw(), request.addressZip())).thenReturn(address);

        AddressResponse response = addressService.findByAddressZipOrAddressRaw(request);

        assertNotNull(response);
        assertEquals(address.getAddressId(), response.addressId());
        assertEquals(address.getAddressZip(), response.addressZip());
        assertEquals(address.getAddressRaw(), response.addressRaw());
    }

    @DisplayName("주소 찾기 (우편번호나 주소를 통해) - 실패 (주소가 존재하지 않음)")
    @Test
    void testFindByAddressZipOrAddressRaw_AddressNotFound() {

        AddressRequest request = AddressRequest.builder()
                .addressZip("12345")
                .addressRaw("Test Address")
                .build();

        when(addressRepository.findAddressByAddressRawOrAddressZip(request.addressRaw(), request.addressZip())).thenReturn(null);

        assertThrows(AddressException.class, () -> {
            addressService.findByAddressZipOrAddressRaw(request);
        });
    }
}
