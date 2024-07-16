package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.ProviderService;
import com.yes255.yes255booksusersserver.presentation.dto.request.provider.CreateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.provider.UpdateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.CreateProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.ProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.UpdateProviderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderControllerTest {

    @InjectMocks
    private ProviderController providerController;

    @Mock
    private ProviderService providerService;

    private CreateProviderRequest createProviderRequest;
    private CreateProviderResponse createProviderResponse;
    private UpdateProviderRequest updateProviderRequest;
    private UpdateProviderResponse updateProviderResponse;
    private ProviderResponse providerResponse;

    @BeforeEach
    void setUp() {
        createProviderRequest = CreateProviderRequest.builder()
                .providerName("Test Provider")
                .build();

        createProviderResponse = CreateProviderResponse.builder()
                .providerName("Test Provider")
                .build();

        updateProviderRequest = UpdateProviderRequest.builder()
                .providerName("Updated Provider")
                .build();

        updateProviderResponse = UpdateProviderResponse.builder()
                .providerName("Updated Provider")
                .build();

        providerResponse = ProviderResponse.builder()
                .providerId(1L)
                .providerName("Test Provider")
                .build();
    }

    @Test
    @DisplayName("제공자 생성 - 성공")
    void testCreateProvider() {
        when(providerService.createProvider(createProviderRequest)).thenReturn(createProviderResponse);

        ResponseEntity<CreateProviderResponse> response = providerController.createProvider(createProviderRequest);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(createProviderResponse, response.getBody());
        verify(providerService).createProvider(createProviderRequest);
    }

    @Test
    @DisplayName("특정 제공자 수정 - 성공")
    void testUpdateProvider() {
        when(providerService.updateProvider(1L, updateProviderRequest)).thenReturn(updateProviderResponse);

        ResponseEntity<UpdateProviderResponse> response = providerController.updateProvider(1L, updateProviderRequest);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(updateProviderResponse, response.getBody());
        verify(providerService).updateProvider(1L, updateProviderRequest);
    }

    @Test
    @DisplayName("특정 제공자 조회 - 성공")
    void testGetProviderById() {
        when(providerService.findProviderById(1L)).thenReturn(providerResponse);

        ResponseEntity<ProviderResponse> response = providerController.getProviderById(1L);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(providerResponse, response.getBody());
        verify(providerService).findProviderById(1L);
    }

    @Test
    @DisplayName("모든 제공자 목록 조회 - 성공")
    void testGetAllProviders() {
        List<ProviderResponse> providerResponses = Collections.singletonList(providerResponse);
        when(providerService.findAllProviders()).thenReturn(providerResponses);

        ResponseEntity<List<ProviderResponse>> response = providerController.getAllProviders();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(providerResponses, response.getBody());
        verify(providerService).findAllProviders();
    }

    @Test
    @DisplayName("특정 제공자 삭제 - 성공")
    void testDeleteProvider() {
        doNothing().when(providerService).deleteProvider(1L);

        ResponseEntity<Void> response = providerController.deleteProvider(1L);

        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
        verify(providerService).deleteProvider(1L);
    }
}
