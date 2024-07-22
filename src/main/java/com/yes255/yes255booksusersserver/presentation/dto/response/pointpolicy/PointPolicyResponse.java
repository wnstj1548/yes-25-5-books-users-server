package com.yes255.yes255booksusersserver.presentation.dto.response.pointpolicy;

import com.yes255.yes255booksusersserver.persistance.domain.PointPolicy;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.CreatePointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.PointPolicyRequest;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record PointPolicyResponse(Long pointPolicyId, String pointPolicyName, BigDecimal pointPolicyApply, String pointPolicyCondition,
                                  boolean pointPolicyApplyType, LocalDate pointPolicyCreatedAt, String pointPolicyUpdatedAt,
                                  BigDecimal pointPolicyConditionAmount, boolean pointPolicyState) {

    public static PointPolicyResponse create(PointPolicy pointPolicy, CreatePointPolicyRequest policyRequest) {
        return PointPolicyResponse.builder()
                .pointPolicyId(pointPolicy.getPointPolicyId())
                .pointPolicyName(policyRequest.pointPolicyName())
                .pointPolicyApply(policyRequest.pointPolicyApply())
                .pointPolicyCondition(policyRequest.pointPolicyCondition())
                .pointPolicyApplyType(policyRequest.pointPolicyApplyType())
                .pointPolicyConditionAmount(policyRequest.pointPolicyConditionAmount())
                .pointPolicyState(true)
                .build();
    }

    public static PointPolicyResponse update(PointPolicy pointPolicy, PointPolicyRequest policyRequest) {
        return PointPolicyResponse.builder()
                .pointPolicyId(pointPolicy.getPointPolicyId())
                .pointPolicyName(policyRequest.pointPolicyName())
                .pointPolicyApply(policyRequest.pointPolicyApply())
                .pointPolicyCondition(policyRequest.pointPolicyCondition())
                .pointPolicyApplyType(policyRequest.pointPolicyApplyType())
                .pointPolicyConditionAmount(policyRequest.pointPolicyConditionAmount())
                .build();
    }

    public static PointPolicyResponse find(PointPolicy pointPolicy) {
        return PointPolicyResponse.builder()
                .pointPolicyId(pointPolicy.getPointPolicyId())
                .pointPolicyName(pointPolicy.getPointPolicyName())
                .pointPolicyApply(pointPolicy.isPointPolicyApplyType() ? pointPolicy.getPointPolicyApplyAmount() : pointPolicy.getPointPolicyRate())
                .pointPolicyCondition(pointPolicy.getPointPolicyCondition())
                .pointPolicyConditionAmount(pointPolicy.getPointPolicyConditionAmount())
                .pointPolicyApplyType(pointPolicy.isPointPolicyApplyType())
                .pointPolicyCreatedAt(pointPolicy.getPointPolicyCreatedAt())
                .pointPolicyUpdatedAt(pointPolicy.getPointPolicyUpdatedAt() != null ? pointPolicy.getPointPolicyUpdatedAt().toString() : null)
                .pointPolicyState(pointPolicy.isPointPolicyState())
                .build();
    }
}
