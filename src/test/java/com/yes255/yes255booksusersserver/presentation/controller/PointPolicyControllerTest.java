package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.PointPolicyService;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.CreatePointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.PointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointpolicy.PointPolicyResponse;
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
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointPolicyControllerTest {

    @InjectMocks
    private PointPolicyController pointPolicyController;

    @Mock
    private PointPolicyService pointPolicyService;

    private PointPolicyResponse pointPolicyResponse;
    private CreatePointPolicyRequest createPointPolicyRequest;
    private PointPolicyRequest pointPolicyRequest;
    private Page<PointPolicyResponse> pointPolicyPage;

    @BeforeEach
    void setUp() {
        pointPolicyResponse = PointPolicyResponse.builder()
                .pointPolicyId(1L)
                .pointPolicyName("Test Policy")
                .pointPolicyApply(BigDecimal.valueOf(10))
                .pointPolicyCondition("Condition")
                .pointPolicyApplyType(true)
                .pointPolicyCreatedAt(LocalDate.now())
                .pointPolicyUpdatedAt("2023-07-15")
                .pointPolicyConditionAmount(BigDecimal.valueOf(100))
                .pointPolicyState(true)
                .build();

        createPointPolicyRequest = CreatePointPolicyRequest.builder()
                .pointPolicyName("Test Policy")
                .pointPolicyApply(BigDecimal.valueOf(10))
                .pointPolicyCondition("Condition")
                .pointPolicyApplyType(true)
                .pointPolicyConditionAmount(BigDecimal.valueOf(100))
                .build();

        pointPolicyRequest = PointPolicyRequest.builder()
                .pointPolicyName("Updated Policy")
                .pointPolicyApply(BigDecimal.valueOf(20))
                .pointPolicyCondition("New Condition")
                .pointPolicyApplyType(false)
                .pointPolicyConditionAmount(BigDecimal.valueOf(200))
                .build();

        pointPolicyPage = new PageImpl<>(Collections.singletonList(pointPolicyResponse));
    }

    @Test
    @DisplayName("포인트 정책 생성 - 성공")
    void testCreatePointPolicy() {
        when(pointPolicyService.createPointPolicy(createPointPolicyRequest)).thenReturn(pointPolicyResponse);

        ResponseEntity<PointPolicyResponse> response = pointPolicyController.createPointPolicy(createPointPolicyRequest);

        assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
        assertEquals(pointPolicyResponse, response.getBody());
        verify(pointPolicyService).createPointPolicy(createPointPolicyRequest);
    }

    @Test
    @DisplayName("특정 포인트 정책 조회 - 성공")
    void testFindPointPolicyById() {
        when(pointPolicyService.findPointPolicyById(1L)).thenReturn(pointPolicyResponse);

        ResponseEntity<PointPolicyResponse> response = pointPolicyController.findPointPolicyById(1L);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(pointPolicyResponse, response.getBody());
        verify(pointPolicyService).findPointPolicyById(1L);
    }

    @Test
    @DisplayName("모든 포인트 정책 목록 조회 - 성공")
    void testFindAllPointPolicies() {
        Pageable pageable = PageRequest.of(0, 10);
        when(pointPolicyService.findAllPointPolicies(pageable)).thenReturn(pointPolicyPage);

        ResponseEntity<Page<PointPolicyResponse>> response = pointPolicyController.findAllPointPolicies(pageable);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(pointPolicyPage, response.getBody());
        verify(pointPolicyService).findAllPointPolicies(pageable);
    }

    @Test
    @DisplayName("특정 포인트 정책 수정 - 성공")
    void testUpdatePointPolicy() {
        when(pointPolicyService.updatePointPolicyById(1L, pointPolicyRequest)).thenReturn(pointPolicyResponse);

        ResponseEntity<PointPolicyResponse> response = pointPolicyController.updatePointPolicy(1L, pointPolicyRequest);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(pointPolicyResponse, response.getBody());
        verify(pointPolicyService).updatePointPolicyById(1L, pointPolicyRequest);
    }

    @Test
    @DisplayName("특정 포인트 정책 삭제 - 성공")
    void testDeletePointPolicy() {
        doNothing().when(pointPolicyService).deletePointPolicyById(1L);

        ResponseEntity<Void> response = pointPolicyController.deletePointPolicy(1L);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        verify(pointPolicyService).deletePointPolicyById(1L);
    }
}
