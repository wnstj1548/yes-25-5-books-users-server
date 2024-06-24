package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.response.pointlog.PointLogResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PointLogService {

    // 포인트 이력 조회
    List<PointLogResponse> findAllPointLogsByUserId(Long userId, Pageable pageable);
}
