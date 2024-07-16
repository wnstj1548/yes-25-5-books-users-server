package com.yes255.yes255booksusersserver.presentation.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.yes255.yes255booksusersserver.application.service.PointLogService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointlog.PointLogResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class PointLogControllerTest {

    @InjectMocks
    private PointLogController pointLogController;

    @Mock
    private PointLogService pointLogService;

    private JwtUserDetails jwtUserDetails;
    private Page<PointLogResponse> pointLogPage;

    @BeforeEach
    void setUp() {
        jwtUserDetails = JwtUserDetails.of(1L, "USER", "accessToken", "refreshToken");

        PointLogResponse pointLogResponse = PointLogResponse.builder()
                .pointCurrent(BigDecimal.valueOf(1000))
                .pointLogUpdatedType("ADD")
                .pointLogAmount(BigDecimal.valueOf(100))
                .pointLogUpdatedAt(LocalDateTime.now())
                .build();

        pointLogPage = new PageImpl<>(Collections.singletonList(pointLogResponse));
    }

    @Test
    @DisplayName("특정 회원의 포인트 내역 조회 - 성공")
    void testGetPointLogs() {
        Pageable pageable = PageRequest.of(0, 10);
        when(pointLogService.findAllPointLogsByUserId(jwtUserDetails.userId(), pageable)).thenReturn(pointLogPage);

        ResponseEntity<Page<PointLogResponse>> response = pointLogController.getPointLogs(pageable, jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(pointLogPage, response.getBody());
        verify(pointLogService).findAllPointLogsByUserId(jwtUserDetails.userId(), pageable);
    }
}
