package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PointPolicyResponse(Long pointPolicyId, String pointPolicyName, String pointPolicyApply, String pointPolicyCondition,
                                  LocalDate pointPolicyCreatedAt, String pointPolicyUpdatedAt) {
}
