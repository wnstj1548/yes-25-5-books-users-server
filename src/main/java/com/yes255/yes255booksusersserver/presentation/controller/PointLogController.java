package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.PointLogService;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointlog.PointLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;

@Tag(name = "포인트 내역 API", description = "포인트 내역 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PointLogController {

    // todo : jwt 토큰으로 변경

    private final PointLogService pointLogService;

    @Operation(summary = "포인트 내역 조회", description = "특정 회원의 포인트 내역을 조회합니다.")
    @GetMapping("/point-logs")
    public ResponseEntity<List<PointLogResponse>> getPointLogs(Pageable pageable) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(pointLogService.findAllPointLogsByUserId(userId, pageable), HttpStatus.OK);
    }
}
