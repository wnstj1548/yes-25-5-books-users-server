package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.ProviderService;
import com.yes255.yes255booksusersserver.presentation.dto.request.provider.CreateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.provider.UpdateProviderRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.CreateProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.ProviderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.provider.UpdateProviderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/providers")
@RequiredArgsConstructor
@RestController
public class ProviderController {

    private final ProviderService providerService;

    // 제공자 생성
    @PostMapping
    public ResponseEntity<CreateProviderResponse> createProvider(@RequestBody CreateProviderRequest providerRequest) {
        return ResponseEntity.ok(providerService.createProvider(providerRequest));
    }

    // 제공자 수정
    @PutMapping("/{providerId}")
    public ResponseEntity<UpdateProviderResponse> updateProvider(@PathVariable Long providerId, @RequestBody UpdateProviderRequest providerRequest) {
        return ResponseEntity.ok(providerService.updateProvider(providerId, providerRequest));
    }

    // 특정 제공자 조회
    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderResponse> getProviderById(@PathVariable Long providerId) {
        return ResponseEntity.ok(providerService.findProviderById(providerId));
    }

    // 제공자 목록 조회
    @GetMapping
    public ResponseEntity<List<ProviderResponse>> getAllProviders() {
        return ResponseEntity.ok(providerService.findAllProviders());
    }

    // 제공자 삭제
    @DeleteMapping("/{providerId}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long providerId) {

        providerService.deleteProvider(providerId);

        return ResponseEntity.noContent().build();
    }
}
