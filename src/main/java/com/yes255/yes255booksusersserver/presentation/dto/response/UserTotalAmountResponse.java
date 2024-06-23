package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserTotalAmountResponse(Long userTotalAmountId, BigDecimal userTotalAmount, Long userId) {
}
