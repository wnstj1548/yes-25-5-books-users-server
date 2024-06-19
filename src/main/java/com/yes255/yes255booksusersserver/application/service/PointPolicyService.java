package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.response.PointLogResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.PointPolicyResponse;

import java.util.List;

public interface PointPolicyService {

    // todo : 포인트 정책 관련 서비스 작성

    // 포인트 정책 작성
    void createPointPolicy();

    // 포인트 정책 목록
    List<PointPolicyResponse> findAllPointPolicies();

    // 포인트 정책 수정

}
