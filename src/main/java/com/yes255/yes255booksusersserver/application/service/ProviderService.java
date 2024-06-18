package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Provider;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.ProviderResponse;

import java.util.List;

public interface ProviderService {

//    CreateProviderResponse createCustomer(CreateProviderRequest providerRequest);
//
//    CreateProviderResponse getCustomer(Long customerId);



    ProviderResponse createProvider(CreateProviderRequest request);

    // todo : Create? or Update?
    ProviderResponse updateProvider(Long providerId, CreateProviderRequest request);
    ProviderResponse getProviderById(Long providerId);
    List<ProviderResponse> getAllProviders();
    void deleteProvider(Long providerId);
}
