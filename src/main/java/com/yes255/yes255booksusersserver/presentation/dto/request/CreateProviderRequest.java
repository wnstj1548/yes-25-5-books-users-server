package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Provider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Builder;

@Builder
public record CreateProviderRequest(String providerName) {

    public Provider toEntity() {
        return Provider.builder()
                .providerName(providerName)
                .build();
    }
}
