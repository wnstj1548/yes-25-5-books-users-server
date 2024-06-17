package com.yes255.yes255booksusersserver.presentation.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Builder;

@Builder
public record CreateProviderRequest(String providerName) {
}
