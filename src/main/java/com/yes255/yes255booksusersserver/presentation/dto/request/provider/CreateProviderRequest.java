package com.yes255.yes255booksusersserver.presentation.dto.request.provider;

import com.yes255.yes255booksusersserver.persistance.domain.Provider;

import lombok.Builder;

@Builder
public record CreateProviderRequest(String providerName) {

    public Provider toEntity() {
        return Provider.builder()
                .providerName(providerName)
                .build();
    }
}
