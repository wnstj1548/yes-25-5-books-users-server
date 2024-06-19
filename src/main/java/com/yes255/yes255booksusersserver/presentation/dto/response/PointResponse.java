package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PointResponse(BigDecimal point) {
}
