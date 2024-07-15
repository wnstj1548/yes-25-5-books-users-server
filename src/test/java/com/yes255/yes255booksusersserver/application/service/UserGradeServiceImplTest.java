package com.yes255.yes255booksusersserver.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.yes255.yes255booksusersserver.application.service.impl.UserGradeServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.common.exception.UserGradeLogException;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.OrderAdaptor;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeLogRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserTotalPureAmountRepository;
import com.yes255.yes255booksusersserver.presentation.dto.response.OrderLogResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.usergrade.UserGradeResponse;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserGradeServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaUserGradeRepository userGradeRepository;

    @Mock
    private JpaUserGradeLogRepository userGradeLogRepository;

    @Mock
    private JpaUserTotalPureAmountRepository userTotalPureAmountRepository;

    @Mock
    private OrderAdaptor orderAdaptor;

    @InjectMocks
    private UserGradeServiceImpl userGradeService;

    private User testUser;
    private UserGrade testUserGrade;

    @BeforeEach
    void setUp() {
        Customer testCustomer = Customer.builder()
                .userId(1L)
                .userRole("MEMBER")
                .build();

        PointPolicy testPointPolicy = PointPolicy.builder()
                .pointPolicyId(1L)
                .pointPolicyName("Test Policy")
                .pointPolicyConditionAmount(new BigDecimal("100"))
                .pointPolicyCondition("Test Condition")
                .pointPolicyApplyAmount(new BigDecimal("10"))
                .pointPolicyCreatedAt(LocalDate.now())
                .pointPolicyApplyType(true)
                .pointPolicyState(true)
                .build();

        testUserGrade = UserGrade.builder()
                .userGradeId(1L)
                .userGradeName("NORMAL")
                .pointPolicy(testPointPolicy)
                .build();

        testUser = User.builder()
                .userId(1L)
                .customer(testCustomer)
                .userName("Test User")
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userRegisterDate(LocalDateTime.now())
                .userPassword("encodedPassword")
                .userGrade(testUserGrade)
                .build();
    }

    @Test
    @DisplayName("회원 등급 조회 - 성공")
    void testGetUserGrade_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        UserGradeResponse response = userGradeService.getUserGrade(1L);

        assertNotNull(response);
        assertEquals(testUserGrade.getUserGradeId(), response.userGradeId());
        assertEquals(testUserGrade.getUserGradeName(), response.userGradeName());
    }

    @Test
    @DisplayName("회원 등급 조회 - 실패 (회원을 찾을 수 없음)")
    void testGetUserGrade_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userGradeService.getUserGrade(1L));
    }

    @Test
    @DisplayName("회원 등급 갱신 - 성공")
    void testUpdateUserGrade_Success() {
        BigDecimal purePrice = new BigDecimal("1500");
        LocalDate currentDate = LocalDate.now();

        UserGrade pointPolicyGrade = UserGrade.builder()
                .pointPolicy(PointPolicy.builder()
                        .pointPolicyState(true)
                        .pointPolicyConditionAmount(new BigDecimal("1000"))
                        .build())
                .build();

        when(userGradeLogRepository.findFirstByUserUserIdOrderByUserGradeUpdatedAtDesc(anyLong()))
                .thenReturn(Optional.of(UserGradeLog.builder().userGradeUpdatedAt(LocalDate.now().minusMonths(3)).build()));
        when(userGradeRepository.findByPointPolicyPointPolicyState(true))
                .thenReturn(Collections.singletonList(pointPolicyGrade));

        userGradeService.updateUserGrade(testUser, purePrice, currentDate);

        assertEquals(pointPolicyGrade, testUser.getUserGrade());
        verify(userRepository, times(1)).save(testUser);
        verify(userGradeLogRepository, times(1)).save(any(UserGradeLog.class));
    }

    @Test
    @DisplayName("회원 등급 갱신 - 실패 (회원 등급 변동 이력 없음)")
    void testUpdateUserGrade_NoGradeLog() {
        when(userGradeLogRepository.findFirstByUserUserIdOrderByUserGradeUpdatedAtDesc(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserGradeLogException.class, () -> userGradeService.updateUserGrade(testUser, BigDecimal.ZERO, LocalDate.now()));
    }

    @Test
    @DisplayName("매달 1일 회원 등급 갱신 - 성공")
    void testUpdateMonthlyGrades() {
        OrderLogResponse orderLogResponse = new OrderLogResponse(1L, new BigDecimal("2000"));

        UserGrade pointPolicyGrade = UserGrade.builder()
                .pointPolicy(PointPolicy.builder()
                        .pointPolicyState(true)
                        .pointPolicyConditionAmount(new BigDecimal("1000"))
                        .build())
                .build();

        when(userGradeLogRepository.findFirstByUserUserIdOrderByUserGradeUpdatedAtDesc(anyLong()))
                .thenReturn(Optional.of(UserGradeLog.builder().userGradeUpdatedAt(LocalDate.now().minusMonths(3)).build()));
        when(userRepository.findById(orderLogResponse.customerId())).thenReturn(Optional.of(testUser));
        when(userGradeRepository.findByPointPolicyPointPolicyState(true)).thenReturn(new ArrayList<>(List.of(testUserGrade, pointPolicyGrade)));
        when(orderAdaptor.getOrderLogs(any())).thenReturn(Collections.singletonList(orderLogResponse));

        userGradeService.updateMonthlyGrades();

        verify(userTotalPureAmountRepository, times(1)).save(any());
    }
}
