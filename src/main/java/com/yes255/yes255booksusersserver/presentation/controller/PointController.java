package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.PointService;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdatePointRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.PointResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdatePointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PointController {

    private final PointService pointService;

    // 특정 사용자 현재 포인트 조회
    @GetMapping("/{userId}/points")
    public ResponseEntity<PointResponse> getPoints(@PathVariable Long userId) {
        return new ResponseEntity<>(pointService.findPointByUserId(userId), HttpStatus.OK);
    }

    // 포인트 사용 및 적립 갱신
//    @PatchMapping("/{userId}")
//    public ResponseEntity<UpdatePointResponse> updatePoint(@PathVariable Long userId, @RequestBody UpdatePointRequest pointRequest) {
//        return new ResponseEntity<>(pointService.updatePointByUserId(userId, pointRequest), HttpStatus.OK);
//    }
}
