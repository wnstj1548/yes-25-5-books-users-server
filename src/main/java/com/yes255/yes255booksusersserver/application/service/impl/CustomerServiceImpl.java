package com.yes255.yes255booksusersserver.application.service.impl;
import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.common.exception.CustomerException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Cart;
import com.yes255.yes255booksusersserver.persistance.domain.CartBook;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import com.yes255.yes255booksusersserver.presentation.dto.request.customer.CustomerRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.CustomerResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.NonMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final JpaCustomerRepository customerRepository;
    private final JpaCartRepository cartRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {

        Customer customer = Customer.builder()
                .userRole(customerRequest.userRole())
                .build();

        customerRepository.save(customer);

        return CustomerResponse.builder()
                .userId(customer.getUserId())
                .userRole(customerRequest.userRole())
                .build();
    }

    @Override
    public CustomerResponse updateCustomer(Long customerId, CustomerRequest customerRequest) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorStatus.toErrorStatus("고객이 존재하지 않습니다.", 400, LocalDateTime.now())));

        customer.updateCustomerUserRole(customerRequest.userRole());

        customerRepository.save(customer);

        return CustomerResponse.builder()
                .userId(customer.getUserId())
                .userRole(customer.getUserRole())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse getCustomerById(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorStatus.toErrorStatus("고객이 존재하지 않습니다.", 400, LocalDateTime.now())));

        return CustomerResponse.builder()
                .userId(customer.getUserId())
                .userRole(customer.getUserRole())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CustomerResponse> getAllCustomers() {

        List<Customer> customers = customerRepository.findAll();

        if (customers.isEmpty()) {
            throw new CustomerException(ErrorStatus.toErrorStatus("고객이 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        return customers.stream()
                .map(customer -> CustomerResponse.builder()
                        .userId(customer.getUserId())
                        .userRole(customer.getUserRole())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCustomer(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorStatus.toErrorStatus("고객이 존재하지 않습니다.", 400, LocalDateTime.now())));

        customerRepository.deleteById(customerId);
    }

    // 비회원 주문 (바로 구매)
    @Override
    public CustomerResponse createNonMember() {

        Customer customer = Customer.builder()
                .userRole("NONMEMBER")
                .build();

        customerRepository.save(customer);

        return CustomerResponse.builder()
                .userId(customer.getUserId())
                .userRole(customer.getUserRole())
                .build();
    }

    // 비회원 주문 (장바구니 이용)
    @Override
    public NonMemberResponse createNonMemberWithCart() {

        Customer customer = Customer.builder()
                .userRole("NONMEMBER")
                .build();
        customerRepository.save(customer);

        Cart cart = Cart.builder()
                .customer(customer)
                .cartCreatedAt(LocalDate.now())
                .build();
        cartRepository.save(cart);

        return NonMemberResponse.builder()
                .userId(customer.getUserId())
                .userRole(customer.getUserRole())
                .cartId(cart.getCartId())
                .build();
    }
}
