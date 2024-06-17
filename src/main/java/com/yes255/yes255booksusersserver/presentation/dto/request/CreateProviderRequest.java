package com.yes255.yes255booksusersserver.presentation.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateProviderRequest {

    private String providerName;

    public CreateProviderRequest(String providerName) {
        this.providerName = providerName;
    }
}
