package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import com.yes255.yes255booksusersserver.persistance.domain.Provider;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateProviderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final JpaCustomerRepository customerRepository;

//    @Override
//    public CreateProviderResponse createCustomer(CreateProviderRequest providerRequest) {
//
//
////        customerRepository.save(provider);
//
//        return null;
//    }
//
//    @Override
//    public CreateProviderResponse getCustomer(Long customerId) {
//        return null;
//    }
}
