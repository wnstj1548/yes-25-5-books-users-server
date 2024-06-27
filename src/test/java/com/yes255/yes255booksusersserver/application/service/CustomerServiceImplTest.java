package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.CustomerServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.CustomerException;
import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.customer.CustomerRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private JpaCustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerRequest customerRequest;

    @BeforeEach
    void setUp() {
        customerRequest = CustomerRequest.builder()
                .userRole("USER")
                .build();
    }

    @DisplayName("고객 생성 - 성공")
    @Test
    void testCreateCustomer_Success() {

        customer = Customer.builder()
                .userId(1L)
                .userRole("USER")
                .build();

        Customer savedCustomer = Customer.builder()
                .userId(1L)
                .userRole("USER")
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponse response = customerService.createCustomer(customerRequest);

        assertNotNull(response);
        assertEquals(savedCustomer.getUserRole(), response.userRole());
    }

    @DisplayName("고객 수정 - 성공")
    @Test
    void testUpdateCustomer_Success() {

        customer = Customer.builder()
                .userId(1L)
                .userRole("USER")
                .build();

        when(customerRepository.findById(customer.getUserId())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = customerService.updateCustomer(customer.getUserId(), customerRequest);

        assertNotNull(response);
        assertEquals(customer.getUserId(), response.userId());
        assertEquals(customerRequest.userRole(), response.userRole());
    }

    @DisplayName("특정 고객 조회 - 성공")
    @Test
    void testGetCustomerById_Success() {

        customer = Customer.builder()
                .userId(1L)
                .userRole("USER")
                .build();

        when(customerRepository.findById(customer.getUserId())).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getCustomerById(customer.getUserId());

        assertNotNull(response);
        assertEquals(customer.getUserId(), response.userId());
        assertEquals(customer.getUserRole(), response.userRole());
    }

    @DisplayName("특정 고객 조회 - 실패")
    @Test
    void testGetCustomerById_NotFound() {

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerException.class, () -> customerService.getCustomerById(1L));
    }

    @DisplayName("모든 고객 목록 조회 - 성공")
    @Test
    void testGetAllCustomers_Success() {

        customer = Customer.builder()
                .userId(1L)
                .userRole("USER")
                .build();

        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        List<CustomerResponse> responses = customerService.getAllCustomers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(customer.getUserId(), responses.getFirst().userId());
        assertEquals(customer.getUserRole(), responses.getFirst().userRole());
    }

    @DisplayName("모든 고객 목록 조회 - 실패")
    @Test
    void testGetAllCustomers_NoCustomers() {

        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(CustomerException.class, () -> customerService.getAllCustomers());
    }

    @DisplayName("특정 고객 삭제 - 성공")
    @Test
    void testDeleteCustomer_Success() {

        customer = Customer.builder()
                .userId(1L)
                .userRole("USER")
                .build();

        when(customerRepository.findById(customer.getUserId())).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(customer.getUserId());

        assertDoesNotThrow(() -> customerService.deleteCustomer(customer.getUserId()));
    }

    @DisplayName("특정 고객 삭제 - 실패")
    @Test
    void testDeleteCustomer_NotFound() {

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerException.class, () -> customerService.deleteCustomer(1L));
    }
}
