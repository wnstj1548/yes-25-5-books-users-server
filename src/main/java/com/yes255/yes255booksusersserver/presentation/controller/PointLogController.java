package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.PointLogService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointlog.PointLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 포인트 내역 관련 API를 제공하는 PointLogController
 */

@Tag(name = "포인트 내역 API", description = "포인트 내역 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PointLogController {

    private final PointLogService pointLogService;

    /**
     * 특정 회원의 포인트 내역을 조회합니다.
     *
     * @param pageable 페이지네이션 정보를 포함하는 Pageable 객체
     * @param jwtUserDetails 유저 토큰 정보
     * @return 포인트 내역 리스트와 상태 코드 200(OK)
     */
    @Operation(summary = "포인트 내역 조회", description = "특정 회원의 포인트 내역을 조회합니다.")
    @GetMapping("/point-logs")
    public ResponseEntity<Page<PointLogResponse>> getPointLogs(Pageable pageable,
                                                               @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return new ResponseEntity<>(pointLogService.findAllPointLogsByUserId(userId, pageable), HttpStatus.OK);
    }
}
