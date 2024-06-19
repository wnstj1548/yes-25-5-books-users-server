package com.yes255.yes255booksusersserver.presentation.dto.request;

import lombok.Builder;

@Builder
public record CreatePointPolicyRateRequest(String pointPolicyName, String pointPolicyRate, String pointPolicyCondition) {
}
