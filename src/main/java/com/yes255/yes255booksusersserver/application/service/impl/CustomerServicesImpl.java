package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CustomerServices;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerServicesImpl implements CustomerServices {

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
