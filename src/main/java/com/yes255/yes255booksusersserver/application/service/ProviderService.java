package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Provider;

import java.util.List;

public interface ProviderService {

//    CreateProviderResponse createCustomer(CreateProviderRequest providerRequest);
//
//    CreateProviderResponse getCustomer(Long customerId);



    Provider createProvider(Provider provider);
    Provider updateProvider(Long providerId, Provider provider);
    Provider getProviderById(Long providerId);
    List<Provider> getAllProviders();
    void deleteProvider(Long providerId);
}
