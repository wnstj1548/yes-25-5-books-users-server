package com.yes255.yes255booksusersserver.application.service.impl;
import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final JpaCustomerRepository customerRepository;

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Long customerId, Customer customer) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 기존 고객 정보를 기반으로 새로운 고객 객체 생성
        Customer updatedCustomer = Customer.builder()
//                .userId(existingCustomer.getUserId())
                .userRole(customer.getUserRole() != null ? customer.getUserRole() : existingCustomer.getUserRole())
//                .users(existingCustomer.getUser())
                .build();

        return customerRepository.save(updatedCustomer);
    }

    @Override
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }
}
