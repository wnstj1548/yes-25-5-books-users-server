package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.OrderUserService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadUserInfoResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserOrderInfoControllerTest {

    @Mock
    private OrderUserService orderUserService;

    @InjectMocks
    private UserOrderInfoController userOrderInfoController;

    private JwtUserDetails jwtUserDetails;
    private ReadOrderUserInfoResponse orderUserInfoResponse;
    private ReadUserInfoResponse userInfoResponse;
    private Page<ReadOrderUserAddressResponse> userAddressResponsePage;

    @BeforeEach
    void setUp() {
        jwtUserDetails = JwtUserDetails.of(1L, "USER", "accessToken", "refreshToken");

        orderUserInfoResponse = ReadOrderUserInfoResponse.builder()
                .userId(1L)
                .name("User")
                .email("user@example.com")
                .phoneNumber("010-1234-5678")
                .points(100)
                .role("ROLE_USER")
                .build();

        userInfoResponse = ReadUserInfoResponse.builder()
                .gradeId(1L)
                .points(100)
                .name("User")
                .userId(1L)
                .build();

        ReadOrderUserAddressResponse addressResponse = ReadOrderUserAddressResponse.builder()
                .userAddressId(1L)
                .addressRaw("123 Street")
                .addressDetail("Apt 456")
                .addressName("Home")
                .zipCode("12345")
                .addressBased(true)
                .build();

        userAddressResponsePage = new PageImpl<>(List.of(addressResponse), PageRequest.of(0, 10), 1);
    }

    @Test
    @DisplayName("주문 고객 정보 조회 - 성공")
    void testGetUserInfo() {
        when(orderUserService.orderUserInfo(anyLong()))
                .thenReturn(orderUserInfoResponse);

        ResponseEntity<ReadOrderUserInfoResponse> response = userOrderInfoController.getUserInfo(jwtUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(orderUserInfoResponse);
    }

    @Test
    @DisplayName("주문 고객 주소 목록 조회 - 성공")
    void testGetUserAddresses() {
        when(orderUserService.getUserAddresses(anyLong(), any(Pageable.class)))
                .thenReturn(userAddressResponsePage);

        Pageable pageable = PageRequest.of(0, 10);
        ResponseEntity<Page<ReadOrderUserAddressResponse>> response = userOrderInfoController.getUserAddresses(pageable, jwtUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userAddressResponsePage);
    }

    @Test
    @DisplayName("주문 고객 포인트, 등급 조회 - 성공")
    void testGetUserPointsAndGrade() {
        when(orderUserService.getUserInfo(anyLong()))
                .thenReturn(userInfoResponse);

        ResponseEntity<ReadUserInfoResponse> response = userOrderInfoController.getUserPointsAndGrade(jwtUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userInfoResponse);
    }
}
