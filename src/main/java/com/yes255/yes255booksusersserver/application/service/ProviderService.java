package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Provider;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateProviderResponse;

import java.util.List;

public interface ProviderService {

    CreateProviderResponse createProvider(CreateProviderRequest request);

    UpdateProviderResponse updateProvider(Long providerId, UpdateProviderRequest request);

    ProviderResponse findProviderById(Long providerId);

    List<ProviderResponse> findAllProviders();

    void deleteProvider(Long providerId);
}
