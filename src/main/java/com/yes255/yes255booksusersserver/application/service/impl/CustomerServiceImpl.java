package com.yes255.yes255booksusersserver.application.service.impl;
import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.common.exception.CustmorNotfoundException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import com.yes255.yes255booksusersserver.presentation.dto.request.customer.CustomerRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final JpaCustomerRepository customerRepository;

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
                .orElseThrow(() -> new CustmorNotfoundException(ErrorStatus.toErrorStatus("고객이 존재하지 않습니다.", 400, LocalDateTime.now())));

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
                .orElseThrow(() -> new CustmorNotfoundException(ErrorStatus.toErrorStatus("고객이 존재하지 않습니다.", 400, LocalDateTime.now())));

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
            throw new CustmorNotfoundException(ErrorStatus.toErrorStatus("고객이 존재하지 않습니다.", 400, LocalDateTime.now()));
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
                .orElseThrow(() -> new CustmorNotfoundException(ErrorStatus.toErrorStatus("고객이 존재하지 않습니다.", 400, LocalDateTime.now())));

        customerRepository.deleteById(customerId);
    }
}
