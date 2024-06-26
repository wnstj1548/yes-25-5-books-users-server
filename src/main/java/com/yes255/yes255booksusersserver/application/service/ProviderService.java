package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.provider.CreateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.provider.UpdateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.CreateProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.ProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.UpdateProviderResponse;

import java.util.List;

public interface ProviderService {

    CreateProviderResponse createProvider(CreateProviderRequest request);

    UpdateProviderResponse updateProvider(Long providerId, UpdateProviderRequest request);

    ProviderResponse findProviderById(Long providerId);

    List<ProviderResponse> findAllProviders();

    void deleteProvider(Long providerId);
}
