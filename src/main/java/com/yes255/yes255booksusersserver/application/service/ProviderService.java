package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Provider;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateProviderResponse;

public interface ProviderService {

    CreateProviderResponse createCustomer(CreateProviderRequest providerRequest);

    CreateProviderResponse getCustomer(Long customerId);
}
