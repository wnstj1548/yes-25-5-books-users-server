package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import com.yes255.yes255booksusersserver.presentation.dto.request.customer.CustomerRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.CustomerResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.NonMemberResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest customerRequest);

    CustomerResponse updateCustomer(Long customerId, CustomerRequest customerRequest);

    CustomerResponse getCustomerById(Long customerId);

    List<CustomerResponse> getAllCustomers();

    void deleteCustomer(Long customerId);

    CustomerResponse createNonMember();

    NonMemberResponse createNonMemberWithCart();
}
