package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.PointServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.PointException;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.*;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaUserGradeRepository userGradeRepository;

    @Mock
    private JpaPointRepository pointRepository;

    @Mock
    private JpaPointPolicyRepository pointPolicyRepository;

    @Mock
    private JpaPointLogRepository pointLogRepository;

    @Mock
    private JpaUserTotalPureAmountRepository totalAmountRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    private final Long userId = 1L;
    private User testUser;
    private Point testPoint;

    @BeforeEach
    void setup() {
        // Customer 설정
        Customer testCustomer = Customer.builder()
                .userId(userId)
                .userRole("USER")
                .build();

        // Provider 설정
        Provider testProvider = Provider.builder()
                .providerId(1L)
                .providerName("TestProvider")
                .build();

        // UserState 설정
        UserState testUserState = UserState.builder()
                .userStateId(1L)
                .userStateName("Active")
                .build();

        // PointPolicy 설정
        PointPolicy testPointPolicy = PointPolicy.builder()
                .pointPolicyName("Standard Policy")
                .pointPolicyRate(BigDecimal.valueOf(0.1))
                .pointPolicyConditionAmount(BigDecimal.valueOf(100))
                .pointPolicyCondition("Purchase over 100")
                .pointPolicyApplyType(false)
                .pointPolicyCreatedAt(LocalDate.now())
                .build();

        // UserGrade 설정
        UserGrade testUserGrade = UserGrade.builder()
                .userGradeId(1L)
                .userGradeName("Gold")
                .pointPolicy(testPointPolicy)
                .build();

        // User 설정
        testUser = User.builder()
                .customer(testCustomer)
                .userName("TestUser")
                .userPhone("010-1234-5678")
                .userEmail("testuser@example.com")
                .userBirth(LocalDate.of(1990, 1, 1))
                .userRegisterDate(LocalDateTime.now())
                .provider(testProvider)
                .userState(testUserState)
                .userGrade(testUserGrade)
                .userPassword("password")
                .build();

        // Point 설정
        testPoint = Point.builder()
                .pointCurrent(BigDecimal.valueOf(100))
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("포인트 조회 - 성공")
    void testFindPointByUserId_Success() {
        when(pointRepository.findByUser_UserId(eq(userId))).thenReturn(testPoint);

        PointResponse response = pointService.findPointByUserId(userId);

        assertEquals(testPoint.getPointCurrent(), response.point());
    }

    @Test
    @DisplayName("포인트 조회 - 실패 (포인트 없음)")
    void testFindPointByUserId_Failure_NoPoint() {
        when(pointRepository.findByUser_UserId(eq(userId))).thenReturn(null);

        assertThrows(PointException.class, () -> pointService.findPointByUserId(userId));
    }

    @Test
    @DisplayName("포인트 사용 및 적립 - 성공")
    void testUpdatePointByUserId_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(testPoint);

        UpdatePointRequest request = UpdatePointRequest.builder()
                .usePoints(BigDecimal.valueOf(10))
                .amount(BigDecimal.valueOf(50))
                .operationType("use")
                .build();

        UpdatePointResponse response = pointService.updatePointByUserId(userId, request);

        BigDecimal expectedPoint = BigDecimal.valueOf(100)
                .add(BigDecimal.valueOf(50).multiply(BigDecimal.valueOf(0.1)))
                .subtract(BigDecimal.valueOf(10));

        assertEquals(expectedPoint, response.point());
    }

    @Test
    @DisplayName("포인트 사용 - 실패 (포인트 부족)")
    void testUpdatePointByUserId_Failure_InsufficientPoints() {
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(eq(userId))).thenReturn(testPoint);

        UpdatePointRequest request = UpdatePointRequest.builder()
                .usePoints(BigDecimal.valueOf(150))
                .amount(BigDecimal.valueOf(0))
                .operationType("use")
                .build();

        assertThrows(PointException.class, () -> pointService.updatePointByUserId(userId, request));
    }

    @Test
    @DisplayName("포인트 사용 및 적립 - 포인트 사용만")
    void testUpdatePointByUserId_OnlyUsePoints() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(testPoint);

        UpdatePointRequest request = UpdatePointRequest.builder()
                .usePoints(BigDecimal.valueOf(20))
                .amount(BigDecimal.valueOf(0))
                .operationType("use")
                .build();

        UpdatePointResponse response = pointService.updatePointByUserId(userId, request);

        BigDecimal expectedPoint = BigDecimal.valueOf(100.0)
                .subtract(BigDecimal.valueOf(20));

        assertEquals(expectedPoint, response.point());
    }

    @Test
    @DisplayName("포인트 사용 및 적립 - 포인트 적립만")
    void testUpdatePointByUserId_OnlyAccumulatePoints() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(testPoint);

        UpdatePointRequest request = UpdatePointRequest.builder()
                .usePoints(BigDecimal.valueOf(0))
                .amount(BigDecimal.valueOf(200))
                .operationType("use")
                .build();

        UpdatePointResponse response = pointService.updatePointByUserId(userId, request);

        BigDecimal expectedPoint = BigDecimal.valueOf(100)
                .add(BigDecimal.valueOf(200).multiply(BigDecimal.valueOf(0.1)));

        assertEquals(expectedPoint, response.point());
    }

    @Test
    @DisplayName("포인트 롤백 - 성공")
    void testUpdatePointByUserId_Rollback_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(testPoint);

        UpdatePointRequest request = UpdatePointRequest.builder()
                .usePoints(BigDecimal.valueOf(10))
                .amount(BigDecimal.valueOf(50))
                .operationType("rollback")
                .build();

        UpdatePointResponse response = pointService.updatePointByUserId(userId, request);

        BigDecimal expectedPoint = BigDecimal.valueOf(100)
                .subtract(BigDecimal.valueOf(50).multiply(BigDecimal.valueOf(0.1)))
                .add(BigDecimal.valueOf(10));

        assertEquals(expectedPoint, response.point());
    }

    @Test
    @DisplayName("포인트 롤백 - 실패 (포인트 부족)")
    void testUpdatePointByUserId_Rollback_Failure_InsufficientPoints() {
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(eq(userId))).thenReturn(testPoint);

        UpdatePointRequest request = UpdatePointRequest.builder()
                .usePoints(BigDecimal.valueOf(0))
                .amount(BigDecimal.valueOf(1500))
                .operationType("rollback")
                .build();

        assertThrows(PointException.class, () -> pointService.updatePointByUserId(userId, request));
    }

    @Test
    @DisplayName("반품 포인트 적립 - 성공")
    void testUpdatePointByRefund_Success() {
        when(pointRepository.findByUser_UserId(eq(userId))).thenReturn(testPoint);

        UpdateRefundRequest request = UpdateRefundRequest.builder()
                .refundAmount(BigDecimal.valueOf(50))
                .build();

        pointService.updatePointByRefund(userId, request);

        BigDecimal expectedPoint = BigDecimal.valueOf(100).add(BigDecimal.valueOf(50));

        assertEquals(expectedPoint, testPoint.getPointCurrent());
    }

    @Test
    @DisplayName("반품 포인트 적립 - 실패 (포인트 없음)")
    void testUpdatePointByRefund_Failure_NoPoint() {
        when(pointRepository.findByUser_UserId(eq(userId))).thenReturn(null);

        UpdateRefundRequest request = UpdateRefundRequest.builder()
                .refundAmount(BigDecimal.valueOf(50))
                .build();

        assertThrows(PointException.class, () -> pointService.updatePointByRefund(userId, request));
    }
}
