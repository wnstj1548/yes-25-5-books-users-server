package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.UpdatePointRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.PointLogResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdatePointResponse;

public interface PointService {


    // todo : 현재 포인트 반환 서비스
    PointLogResponse findAllPointLogByUserId(Long userId);

    // 포인트 업데이트하고 현재 포인트 반환
    UpdatePointResponse updatePointByUserId(Long UserId, UpdatePointRequest pointRequest);

    void createPointByUserId(Long userId);
}
