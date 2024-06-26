package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.PointService;
import com.yes255.yes255booksusersserver.presentation.dto.request.point.UpdatePointRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.point.PointResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.point.UpdatePointResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * 포인트 관련 API를 제공하는 PointController
 */

@Tag(name = "포인트 API", description = "포인트 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PointController {

    private final PointService pointService;

    /**
     * 특정 회원의 현재 포인트를 조회합니다.
     *
     * @return 현재 포인트 정보와 상태 코드 200(OK)
     */
    @Operation(summary = "현재 포인트 조회", description = "특정 회원의 현재 포인트를 조회합니다.")
    @GetMapping("/points")
    public ResponseEntity<PointResponse> getPoints() {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(pointService.findPointByUserId(userId), HttpStatus.OK);
    }

    /**
     * 특정 회원의 포인트 사용 및 적립 내역을 갱신합니다.
     *
     * @param pointRequest 포인트 사용 및 적립 요청 정보
     * @return 갱신된 포인트 정보와 상태 코드 200(OK)
     */
    @Operation(summary = "포인트 사용 및 적립 내역 갱신", description = "특정 회원의 포인트 사용 및 적립 내역을 갱신합니다.")
    @PatchMapping("/points")
    public ResponseEntity<UpdatePointResponse> updatePoint(@RequestBody UpdatePointRequest pointRequest) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(pointService.updatePointByUserId(userId, pointRequest), HttpStatus.OK);
    }
}
