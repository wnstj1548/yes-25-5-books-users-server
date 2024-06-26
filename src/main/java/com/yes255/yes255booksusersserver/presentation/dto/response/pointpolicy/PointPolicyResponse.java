package com.yes255.yes255booksusersserver.presentation.dto.response.pointpolicy;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record PointPolicyResponse(Long pointPolicyId, String pointPolicyName, BigDecimal pointPolicyApply, String pointPolicyCondition,
                                  boolean pointPolicyApplyType, LocalDate pointPolicyCreatedAt, String pointPolicyUpdatedAt,
                                  BigDecimal pointPolicyConditionAmount) {
}
