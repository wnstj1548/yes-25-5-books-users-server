package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.ProviderService;
import com.yes255.yes255booksusersserver.common.exception.ProviderException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Provider;
import com.yes255.yes255booksusersserver.persistance.repository.JpaProviderRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.provider.CreateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.provider.UpdateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.CreateProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.ProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.UpdateProviderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final JpaProviderRepository providerRepository;

    @Override
    public CreateProviderResponse createProvider(CreateProviderRequest request) {

        Provider provider = request.toEntity();
        providerRepository.save(provider);

        return CreateProviderResponse.builder()
                .providerName(request.providerName())
                .build();
    }

    @Override
    public UpdateProviderResponse updateProvider(Long providerId, UpdateProviderRequest request) {

        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderException(ErrorStatus.toErrorStatus("제공자가 존재하지 않습니다.", 400, LocalDateTime.now())));

        provider.updateProviderName(request.providerName());
        providerRepository.save(provider);

        return UpdateProviderResponse.builder()
                .providerName(request.providerName())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public ProviderResponse findProviderById(Long providerId) {

        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderException(ErrorStatus.toErrorStatus("제공자가 존재하지 않습니다.", 400, LocalDateTime.now())));

        return ProviderResponse.builder()
                .providerId(provider.getProviderId())
                .providerName(provider.getProviderName())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProviderResponse> findAllProviders() {

        List<Provider> providers = providerRepository.findAll();

        return providers.stream()
                .map(provider -> new ProviderResponse(provider.getProviderId(), provider.getProviderName()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProvider(Long providerId) {

        providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderException(ErrorStatus.toErrorStatus("제공자가 존재하지 않습니다.", 400, LocalDateTime.now())));

        providerRepository.deleteById(providerId);
    }
}
