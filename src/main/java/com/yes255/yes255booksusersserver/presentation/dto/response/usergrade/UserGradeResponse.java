package com.yes255.yes255booksusersserver.presentation.dto.response.usergrade;

import lombok.Builder;

@Builder
public record UserGradeResponse(Long userGradeId, String userGradeName, Long pointPolicyId) {
}
