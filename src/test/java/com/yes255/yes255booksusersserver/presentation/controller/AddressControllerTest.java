package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.AddressService;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.AddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.CreateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.UpdateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.AddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.CreateAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.UpdateAddressResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("주소 등록 - 성공")
    @Test
    void createAddress_success() {
        // given
        CreateAddressRequest request = new CreateAddressRequest("12345", "서울특별시 종로구 종로길");
        CreateAddressResponse response = new CreateAddressResponse("12345", "서울특별시 종로구 종로길");

        when(addressService.createAddress(any(CreateAddressRequest.class))).thenReturn(response);

        // when
        ResponseEntity<CreateAddressResponse> responseEntity = addressController.createAddress(request);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @DisplayName("주소 수정 - 성공")
    @Test
    void updateAddress_success() {
        // given
        Long addressId = 1L;
        UpdateAddressRequest request = new UpdateAddressRequest("67890", "광주광역시 동구 서석동");
        UpdateAddressResponse response = new UpdateAddressResponse(1L, "67890", "광주광역시 동구 서석동");

        when(addressService.updateAddress(anyLong(), any(UpdateAddressRequest.class))).thenReturn(response);

        // when
        ResponseEntity<UpdateAddressResponse> responseEntity = addressController.updateAddress(addressId, request);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @DisplayName("특정 주소 조회 - 성공")
    @Test
    void findAddressById_success() {
        // given
        Long addressId = 1L;
        AddressResponse response = new AddressResponse(1L, "12345", "서울특별시 종로구 종로길");

        when(addressService.findAddressById(anyLong())).thenReturn(response);

        // when
        ResponseEntity<AddressResponse> responseEntity = addressController.findAddressById(addressId);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @DisplayName("모든 주소 목록 조회 - 성공")
    @Test
    void findAllAddresses_success() {
        // given
        List<AddressResponse> responses = List.of(
                new AddressResponse(1L, "12345", "서울특별시 종로구 종로길"),
                new AddressResponse(2L, "67890", "광주광역시 동구 서석동")
        );

        when(addressService.findAllAddresses()).thenReturn(responses);

        // when
        ResponseEntity<List<AddressResponse>> responseEntity = addressController.findAllAddresses();

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responses, responseEntity.getBody());
    }

    @DisplayName("주소 삭제 - 성공")
    @Test
    void deleteAddress_success() {
        // given
        Long addressId = 1L;
        doNothing().when(addressService).deleteAddress(anyLong());

        // when
        ResponseEntity<Void> responseEntity = addressController.deleteAddress(addressId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(addressService, times(1)).deleteAddress(addressId);
    }

    @DisplayName("주소 조회 (우편주소나 일반 주소를 통해) - 성공")
    @Test
    void findAddress_success() {
        // given
        AddressRequest request = new AddressRequest("12345", "서울특별시 종로구 종로길");
        AddressResponse response = new AddressResponse(1L, "12345", "서울특별시 종로구 종로길");

        when(addressService.findByAddressZipOrAddressRaw(any(AddressRequest.class))).thenReturn(response);

        // when
        ResponseEntity<AddressResponse> responseEntity = addressController.findAddress(request);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }
}
