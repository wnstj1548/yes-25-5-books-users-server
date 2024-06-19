package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PointLogResponse(BigDecimal pointCurrent, String pointLogUpdatedType, BigDecimal pointLogAmount,
                               LocalDateTime pointLogUpdatedAt, String pointLogUsed) {
}
