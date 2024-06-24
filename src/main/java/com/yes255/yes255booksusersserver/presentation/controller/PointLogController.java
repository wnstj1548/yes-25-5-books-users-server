package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.PointLogService;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointlog.PointLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PointLogController {

    private final PointLogService pointLogService;

    @GetMapping("/{userId}/point-logs")
    public ResponseEntity<List<PointLogResponse>> getPointLogs(@PathVariable Long userId,
                                                               Pageable pageable) {
        return new ResponseEntity<>(pointLogService.findAllPointLogsByUserId(userId, pageable), HttpStatus.OK);
    }
}
