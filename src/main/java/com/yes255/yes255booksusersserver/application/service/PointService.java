package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.UpdatePointRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.PointResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdatePointResponse;

public interface PointService {

    PointResponse findPointByUserId(Long userId);

    // 구매시 포인트 업데이트하고 현재 포인트 반환
//    UpdatePointResponse updatePointByUserId(Long UserId, UpdatePointRequest pointRequest);
}
