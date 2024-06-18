package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.ProviderService;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.ProviderResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderServicesImpl implements ProviderService {
    @Override
    public ProviderResponse createProvider(CreateProviderRequest request) {
        return null;
    }

    @Override
    public ProviderResponse updateProvider(Long providerId, UpdateProviderRequest request) {
        return null;
    }

    @Override
    public ProviderResponse getProviderById(Long providerId) {
        return null;
    }

    @Override
    public List<ProviderResponse> getAllProviders() {
        return List.of();
    }

    @Override
    public void deleteProvider(Long providerId) {

    }
}
