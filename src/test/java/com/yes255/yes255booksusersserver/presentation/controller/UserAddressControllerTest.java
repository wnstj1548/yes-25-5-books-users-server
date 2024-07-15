package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateAddressBasedRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UpdateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UserAddressResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAddressControllerTest {

    @InjectMocks
    private UserAddressController userAddressController;

    @Mock
    private UserAddressService userAddressService;

    private CreateUserAddressRequest createUserAddressRequest;
    private CreateUserAddressResponse createUserAddressResponse;
    private UpdateUserAddressRequest updateUserAddressRequest;
    private UpdateUserAddressResponse updateUserAddressResponse;
    private UserAddressResponse userAddressResponse;
    private UpdateAddressBasedRequest updateAddressBasedRequest;
    private JwtUserDetails jwtUserDetails;

    @BeforeEach
    void setUp() {
        createUserAddressRequest = CreateUserAddressRequest.builder()
                .addressZip("12345")
                .addressRaw("123 Main St")
                .addressName("Home")
                .addressDetail("Apt 1")
                .addressBased(true)
                .build();

        createUserAddressResponse = CreateUserAddressResponse.builder()
                .addressZip("12345")
                .addressRaw("123 Main St")
                .addressName("Home")
                .addressDetail("Apt 1")
                .addressBased(true)
                .build();

        updateUserAddressRequest = UpdateUserAddressRequest.builder()
                .addressZip("54321")
                .addressRaw("321 Main St")
                .addressName("Office")
                .addressDetail("Suite 2")
                .addressBased(false)
                .build();

        updateUserAddressResponse = UpdateUserAddressResponse.builder()
                .addressZip("54321")
                .addressRaw("321 Main St")
                .addressName("Office")
                .addressDetail("Suite 2")
                .addressBased(false)
                .build();

        userAddressResponse = UserAddressResponse.builder()
                .userAddressId(1L)
                .addressId(1L)
                .addressZip("12345")
                .addressRaw("123 Main St")
                .addressName("Home")
                .addressDetail("Apt 1")
                .addressBased(true)
                .userId(1L)
                .build();

        updateAddressBasedRequest = UpdateAddressBasedRequest.builder()
                .addressBased(true)
                .build();

        jwtUserDetails = mock(JwtUserDetails.class);
        when(jwtUserDetails.userId()).thenReturn(1L);
    }

    @Test
    @DisplayName("회원 주소 생성 - 성공")
    void testCreateUserAddress() {
        when(userAddressService.createAddress(1L, createUserAddressRequest)).thenReturn(createUserAddressResponse);

        ResponseEntity<CreateUserAddressResponse> response = userAddressController.createUserAddress(createUserAddressRequest, jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(createUserAddressResponse, response.getBody());
        verify(userAddressService).createAddress(1L, createUserAddressRequest);
    }

    @Test
    @DisplayName("특정 회원 주소 수정 - 성공")
    void testUpdateUserAddress() {
        when(userAddressService.updateAddress(1L, 1L, updateUserAddressRequest)).thenReturn(updateUserAddressResponse);

        ResponseEntity<UpdateUserAddressResponse> response = userAddressController.updateUserAddress(1L, updateUserAddressRequest, jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(updateUserAddressResponse, response.getBody());
        verify(userAddressService).updateAddress(1L, 1L, updateUserAddressRequest);
    }

    @Test
    @DisplayName("특정 회원 주소 조회 - 성공")
    void testFindUserAddressById() {
        when(userAddressService.findAddressById(1L, 1L)).thenReturn(userAddressResponse);

        ResponseEntity<UserAddressResponse> response = userAddressController.findUserAddressById(1L, jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(userAddressResponse, response.getBody());
        verify(userAddressService).findAddressById(1L, 1L);
    }

    @Test
    @DisplayName("모든 회원 주소 목록 조회 - 성공")
    void testFindAllUserAddresses() {
        Page<UserAddressResponse> userAddressResponses = new PageImpl<>(Collections.singletonList(userAddressResponse));
        when(userAddressService.findAllAddresses(1L, PageRequest.of(0, 10))).thenReturn(userAddressResponses);

        ResponseEntity<Page<UserAddressResponse>> response = userAddressController.findAllUserAddresses(PageRequest.of(0, 10), jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(userAddressResponses, response.getBody());
        verify(userAddressService).findAllAddresses(1L, PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("특정 회원 주소 삭제 - 성공")
    void testDeleteUserAddress() {
        doNothing().when(userAddressService).deleteAddress(1L, 1L);

        ResponseEntity<Void> response = userAddressController.deleteUserAddress(1L, jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
        verify(userAddressService).deleteAddress(1L, 1L);
    }

    @Test
    @DisplayName("특정 회원 주소를 기본 배송지 지정 - 성공")
    void testUpdateAddressBased() {
        doNothing().when(userAddressService).updateAddressBased(1L, 1L, updateAddressBasedRequest);

        ResponseEntity<Void> response = userAddressController.updateAddressBased(1L, updateAddressBasedRequest, jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        verify(userAddressService).updateAddressBased(1L, 1L, updateAddressBasedRequest);
    }
}
