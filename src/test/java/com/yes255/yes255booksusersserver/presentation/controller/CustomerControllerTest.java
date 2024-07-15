package com.yes255.yes255booksusersserver.presentation.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.presentation.dto.request.customer.CustomerRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.CustomerResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.NonMemberResponse;
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
class CustomerControllerTest {

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private CustomerService customerService;

    private CustomerRequest customerRequest;
    private CustomerResponse customerResponse;
    private NonMemberResponse nonMemberResponse;

    @BeforeEach
    void setUp() {
        customerRequest = CustomerRequest.builder()
                .userRole("USER")
                .build();

        customerResponse = CustomerResponse.builder()
                .customerId(1L)
                .role("USER")
                .build();

        nonMemberResponse = NonMemberResponse.builder()
                .userId(1L)
                .userRole("NON_MEMBER")
                .cartId(1L)
                .build();
    }

    @Test
    @DisplayName("고객 생성 - 성공")
    void testCreateCustomer() {
        when(customerService.createCustomer(customerRequest)).thenReturn(customerResponse);

        ResponseEntity<CustomerResponse> response = customerController.createCustomer(customerRequest);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(customerResponse, response.getBody());
        verify(customerService).createCustomer(customerRequest);
    }

    @Test
    @DisplayName("특정 고객 정보 수정 - 성공")
    void testUpdateCustomer() {
        Long customerId = 1L;
        when(customerService.updateCustomer(customerId, customerRequest)).thenReturn(customerResponse);

        ResponseEntity<CustomerResponse> response = customerController.updateCustomer(customerId, customerRequest);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(customerResponse, response.getBody());
        verify(customerService).updateCustomer(customerId, customerRequest);
    }

    @Test
    @DisplayName("특정 고객 정보 조회 - 성공")
    void testGetCustomerById() {
        Long customerId = 1L;
        when(customerService.getCustomerById(customerId)).thenReturn(customerResponse);

        ResponseEntity<CustomerResponse> response = customerController.getCustomerById(customerId);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(customerResponse, response.getBody());
        verify(customerService).getCustomerById(customerId);
    }

    @Test
    @DisplayName("모근 고객 목록 조회 - 성공")
    void testGetAllCustomers() {
        List<CustomerResponse> customerList = Collections.singletonList(customerResponse);
        when(customerService.getAllCustomers()).thenReturn(customerList);

        ResponseEntity<List<CustomerResponse>> response = customerController.getAllCustomers();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(customerList, response.getBody());
        verify(customerService).getAllCustomers();
    }

    @Test
    @DisplayName("특정 고객 정보 삭제 - 성공")
    void testDeleteCustomer() {
        Long customerId = 1L;

        ResponseEntity<Void> response = customerController.deleteCustomer(customerId);

        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
        verify(customerService).deleteCustomer(customerId);
    }

    @Test
    @DisplayName("비회원 고객 생성 (바로 구매용) - 성공")
    void testCreateNonMember() {
        when(customerService.createNonMember()).thenReturn(customerResponse);

        ResponseEntity<CustomerResponse> response = customerController.createNonMember();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(customerResponse, response.getBody());
        verify(customerService).createNonMember();
    }

    @Test
    @DisplayName("비회원 고객 생성 (장바구니용) - 성공")
    void testCreateNonMemberWithCart() {
        when(customerService.createNonMemberWithCart()).thenReturn(nonMemberResponse);

        ResponseEntity<NonMemberResponse> response = customerController.createNonMemberWithCart();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(nonMemberResponse, response.getBody());
        verify(customerService).createNonMemberWithCart();
    }
}
