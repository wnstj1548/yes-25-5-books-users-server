package com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy;

import com.yes255.yes255booksusersserver.persistance.domain.PointPolicy;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder

public record PointPolicyRequest(String pointPolicyName, BigDecimal pointPolicyApply,
                                 String pointPolicyCondition, Boolean pointPolicyApplyType,
                                 BigDecimal pointPolicyConditionAmount) {

    public PointPolicy toEntity() {
        return PointPolicy.builder()
                .pointPolicyName(pointPolicyName)
                .pointPolicyApplyAmount(pointPolicyApplyType ? pointPolicyApply : null)
                .pointPolicyRate(!pointPolicyApplyType ? pointPolicyApply: null)
                .pointPolicyCondition(pointPolicyCondition)
                .pointPolicyCreatedAt(LocalDate.now())
                .pointPolicyConditionAmount(pointPolicyConditionAmount)
                .pointPolicyApplyType(true)
                .build();
    }
}
