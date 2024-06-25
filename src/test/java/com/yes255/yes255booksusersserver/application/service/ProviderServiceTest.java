package com.yes255.yes255booksusersserver.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yes255.yes255booksusersserver.application.service.impl.ProviderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
public class ProviderServiceTest {

    @Mock
    private JpaProviderRepository providerRepository;

    @InjectMocks
    private ProviderServiceImpl providerService;

    @DisplayName("제공자 생성")
    @Test
    void testCreateProvider() {

        CreateProviderRequest request = CreateProviderRequest.builder()
                .providerName("Test Provider")
                .build();

        Provider savedProvider = Provider.builder()
                .providerId(1L)
                .providerName("Test Provider")
                .build();

        when(providerRepository.save(any(Provider.class))).thenReturn(savedProvider);

        assertDoesNotThrow(() -> providerService.createProvider(request));
    }

    @DisplayName("특정 제공자 업데이트")
    @Test
    void testUpdateProvider() {

        Long providerId = 1L;
        UpdateProviderRequest request = UpdateProviderRequest.builder()
                .providerName("Updated Provider")
                .build();

        Provider existingProvider = Provider.builder()
                .providerId(providerId)
                .providerName("Old Provider")
                .build();

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(existingProvider));
        when(providerRepository.save(any(Provider.class))).thenReturn(existingProvider);

        assertDoesNotThrow(() -> providerService.updateProvider(providerId, request));
    }

    @DisplayName("특정 제공자 조회")
    @Test
    void testFindProviderById() {

        Long providerId = 1L;
        Provider existingProvider = Provider.builder()
                .providerId(providerId)
                .providerName("Test Provider")
                .build();

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(existingProvider));

        ProviderResponse response = providerService.findProviderById(providerId);

        assertEquals(providerId, response.providerId());
        assertEquals("Test Provider", response.providerName());
    }

    @DisplayName("모든 제공자 목록 조회")
    @Test
    void testFindAllProviders() {

        List<Provider> providerList = Arrays.asList(
                Provider.builder().providerId(1L).providerName("Provider 1").build(),
                Provider.builder().providerId(2L).providerName("Provider 2").build()
        );

        when(providerRepository.findAll()).thenReturn(providerList);

        List<ProviderResponse> responseList = providerService.findAllProviders();

        assertEquals(2, responseList.size());
        assertEquals(providerList.get(0).getProviderId(), responseList.get(0).providerId());
        assertEquals(providerList.get(0).getProviderName(), responseList.get(0).providerName());
        assertEquals(providerList.get(1).getProviderId(), responseList.get(1).providerId());
        assertEquals(providerList.get(1).getProviderName(), responseList.get(1).providerName());
    }

    @DisplayName("특정 제공자 삭제")
    @Test
    void testDeleteProvider() {

        Long providerId = 1L;
        Provider existingProvider = Provider.builder()
                .providerId(providerId)
                .providerName("Test Provider")
                .build();

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(existingProvider));
        doNothing().when(providerRepository).deleteById(providerId);

        assertDoesNotThrow(() -> providerService.deleteProvider(providerId));
    }
}
