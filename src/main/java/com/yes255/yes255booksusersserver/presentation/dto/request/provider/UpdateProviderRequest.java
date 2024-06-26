package com.yes255.yes255booksusersserver.presentation.dto.request.provider;

import lombok.Builder;

@Builder
public record UpdateProviderRequest(String providerName) {
}
