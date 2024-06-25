package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReaderOrderUserInfoResponse(Long userId, Integer points, List<String> addressRaw,
                                          List<String> addressDetail, List<String> addressName,
                                          List<String> zipCode, List<Boolean> addressBased) {
}
