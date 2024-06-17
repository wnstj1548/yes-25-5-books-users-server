package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long customerId, Customer customer);
    Customer getCustomerById(Long customerId);
    List<Customer> getAllCustomers();
    void deleteCustomer(Long customerId);
}
