package com.yes255.yes255booksusersserver.presentation.dto.request.point;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdatePointRequest(BigDecimal usePoints, BigDecimal amount, String operationType) {
}
