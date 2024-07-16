package com.yes255.yes255booksusersserver.presentation.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.yes255.yes255booksusersserver.application.service.AddressService;
import com.yes255.yes255booksusersserver.presentation.controller.AddressController;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.AddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.CreateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.CreateAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.AddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.UpdateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.UpdateAddressResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @InjectMocks
    private AddressController addressController;

    @Mock
    private AddressService addressService;

    private CreateAddressRequest createAddressRequest;
    private CreateAddressResponse createAddressResponse;
    private UpdateAddressRequest updateAddressRequest;
    private UpdateAddressResponse updateAddressResponse;
    private AddressRequest addressRequest;
    private AddressResponse addressResponse;

    @BeforeEach
    void setUp() {
        createAddressRequest = CreateAddressRequest.builder()
                .addressZip("12345")
                .addressRaw("Sample Address")
                .build();

        createAddressResponse = CreateAddressResponse.builder()
                .addressZip("12345")
                .addressRaw("Sample Address")
                .build();

        updateAddressRequest = UpdateAddressRequest.builder()
                .addressZip("54321")
                .addressRaw("Updated Address")
                .build();

        updateAddressResponse = UpdateAddressResponse.builder()
                .addressId(1L)
                .addressZip("54321")
                .addressRaw("Updated Address")
                .build();

        addressRequest = AddressRequest.builder()
                .addressZip("12345")
                .addressRaw("Sample Address")
                .build();

        addressResponse = AddressResponse.builder()
                .addressId(1L)
                .addressZip("12345")
                .addressRaw("Sample Address")
                .build();
    }

    @Test
    @DisplayName("주소 생성 - 성공")
    void testCreateAddress() {
        when(addressService.createAddress(createAddressRequest)).thenReturn(createAddressResponse);

        ResponseEntity<CreateAddressResponse> response = addressController.createAddress(createAddressRequest);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(createAddressResponse, response.getBody());
        verify(addressService).createAddress(createAddressRequest);
    }

    @Test
    @DisplayName("특정 주소 업데이트 - 성공")
    void testUpdateAddress() {
        Long addressId = 1L;
        when(addressService.updateAddress(addressId, updateAddressRequest)).thenReturn(updateAddressResponse);

        ResponseEntity<UpdateAddressResponse> response = addressController.updateAddress(addressId, updateAddressRequest);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(updateAddressResponse, response.getBody());
        verify(addressService).updateAddress(addressId, updateAddressRequest);
    }

    @Test
    @DisplayName("특정 주소 조회 - 성공")
    void testFindAddressById() {
        Long addressId = 1L;
        when(addressService.findAddressById(addressId)).thenReturn(addressResponse);

        ResponseEntity<AddressResponse> response = addressController.findAddressById(addressId);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(addressResponse, response.getBody());
        verify(addressService).findAddressById(addressId);
    }

    @Test
    @DisplayName("모든 주소 목록 조회 - 성공")
    void testFindAllAddresses() {
        List<AddressResponse> addressList = Collections.singletonList(addressResponse);
        when(addressService.findAllAddresses()).thenReturn(addressList);

        ResponseEntity<List<AddressResponse>> response = addressController.findAllAddresses();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(addressList, response.getBody());
        verify(addressService).findAllAddresses();
    }

    @Test
    @DisplayName("주소 삭제 - 성공")
    void testDeleteAddress() {
        Long addressId = 1L;

        ResponseEntity<Void> response = addressController.deleteAddress(addressId);

        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
        verify(addressService).deleteAddress(addressId);
    }

    @Test
    @DisplayName("우편주소 또는 일반주소로 주소 조회 - 성공")
    void testFindAddress() {
        when(addressService.findByAddressZipOrAddressRaw(addressRequest)).thenReturn(addressResponse);

        ResponseEntity<AddressResponse> response = addressController.findAddress(addressRequest);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(addressResponse, response.getBody());
        verify(addressService).findByAddressZipOrAddressRaw(addressRequest);
    }
}
