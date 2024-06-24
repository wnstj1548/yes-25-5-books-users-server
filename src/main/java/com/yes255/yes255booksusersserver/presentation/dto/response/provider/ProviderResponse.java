package com.yes255.yes255booksusersserver.presentation.dto.response.provider;

import lombok.Builder;

@Builder
public record ProviderResponse(Long providerId, String providerName) {
}
