package com.yes255.yes255booksusersserver.presentation.dto.request.couponuser;

import lombok.Builder;

@Builder
public record UpdateCouponRequest(Long couponId, String operationType) {
}




// todo : 테스트 코드 작성
// todo :쿠폰함 프론트 수정, 코드 컨벤션에 맞춰 코드 수정?