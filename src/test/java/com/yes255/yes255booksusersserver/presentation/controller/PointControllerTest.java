package com.yes255.yes255booksusersserver.presentation.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.yes255.yes255booksusersserver.application.service.PointService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateRefundRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.point.UpdatePointRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.point.PointResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.point.UpdatePointResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class PointControllerTest {

    @InjectMocks
    private PointController pointController;

    @Mock
    private PointService pointService;

    private JwtUserDetails jwtUserDetails;
    private PointResponse pointResponse;
    private UpdatePointRequest updatePointRequest;
    private UpdatePointResponse updatePointResponse;
    private UpdateRefundRequest updateRefundRequest;

    @BeforeEach
    void setUp() {
        jwtUserDetails = JwtUserDetails.of(1L, "USER", "accessToken", "refreshToken");

        pointResponse = PointResponse.builder()
                .point(BigDecimal.valueOf(1000))
                .build();

        updatePointRequest = UpdatePointRequest.builder()
                .usePoints(BigDecimal.valueOf(100))
                .amount(BigDecimal.valueOf(200))
                .operationType("ADD")
                .build();

        updatePointResponse = UpdatePointResponse.builder()
                .point(BigDecimal.valueOf(1100))
                .build();

        updateRefundRequest = UpdateRefundRequest.builder()
                .refundAmount(BigDecimal.valueOf(50))
                .build();
    }

    @Test
    @DisplayName("특정 회원의 현재 포인트를 조회 - 성공")
    void testGetPoints() {
        when(pointService.findPointByUserId(jwtUserDetails.userId())).thenReturn(pointResponse);

        ResponseEntity<PointResponse> response = pointController.getPoints(jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(pointResponse, response.getBody());
        verify(pointService).findPointByUserId(jwtUserDetails.userId());
    }

    @Test
    @DisplayName("특정 회원의 포인트 사용 및 적립 내역 갱신 - 성공")
    void testUpdatePoint() {
        when(pointService.updatePointByUserId(jwtUserDetails.userId(), updatePointRequest)).thenReturn(updatePointResponse);

        ResponseEntity<UpdatePointResponse> response = pointController.updatePoint(updatePointRequest, jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(updatePointResponse, response.getBody());
        verify(pointService).updatePointByUserId(jwtUserDetails.userId(), updatePointRequest);
    }

    @Test
    @DisplayName("특정 회원의 포인트 환불 적용 및 내역 갱신 - 성공")
    void testUpdatePointsRefund() {
        doNothing().when(pointService).updatePointByRefund(jwtUserDetails.userId(), updateRefundRequest);

        ResponseEntity<Void> response = pointController.updatePointsRefund(updateRefundRequest, jwtUserDetails);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        verify(pointService).updatePointByRefund(jwtUserDetails.userId(), updateRefundRequest);
    }
}
