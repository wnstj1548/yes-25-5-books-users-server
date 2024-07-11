package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.UserTotalPureAmountServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.UserTotalPureAmountException;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserTotalPureAmount;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserTotalPureAmountRepository;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadPurePriceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserTotalPureAmountServiceImplTest {

    @Mock
    private JpaUserTotalPureAmountRepository userTotalAmountRepository;

    @InjectMocks
    private UserTotalPureAmountServiceImpl userTotalPureAmountService;

    private UserTotalPureAmount testUserTotalPureAmount;

    @BeforeEach
    void setup() {
        User testUser = User.builder()
                .userId(1L)
                .userName("Test User")
                .build();

        testUserTotalPureAmount = UserTotalPureAmount.builder()
                .user(testUser)
                .userTotalPureAmount(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    @DisplayName("회원 3개월치 순수 금액 반환 - 성공")
    void testFindUserTotalPureAmountByUserId_Success() {
        when(userTotalAmountRepository.findFirstByUserUserIdOrderByUserTotalPureAmountRecordedAtDesc(anyLong()))
                .thenReturn(Optional.of(testUserTotalPureAmount));

        ReadPurePriceResponse response = userTotalPureAmountService.findUserTotalPureAmountByUserId(1L);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1000), response.purePrice());
        assertEquals(LocalDate.now(), response.recordedAt());
    }

    @Test
    @DisplayName("회원 3개월치 순수 금액 반환 - 실패")
    void testFindUserTotalPureAmountByUserId_Failure() {
        when(userTotalAmountRepository.findFirstByUserUserIdOrderByUserTotalPureAmountRecordedAtDesc(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserTotalPureAmountException.class, () ->
                userTotalPureAmountService.findUserTotalPureAmountByUserId(1L));
    }

    @Test
    @DisplayName("회원 순수 누적 금액 삭제 - 성공")
    void testDeleteUserTotalPureAmountByUserId_Success() {
        when(userTotalAmountRepository.findByUserUserId(anyLong()))
                .thenReturn(Optional.of(testUserTotalPureAmount));

        doNothing().when(userTotalAmountRepository).delete(testUserTotalPureAmount);

        userTotalPureAmountService.deleteUserTotalPureAmountByUserId(1L);

        verify(userTotalAmountRepository, times(1)).delete(testUserTotalPureAmount);
    }

    @Test
    @DisplayName("회원 순수 누적 금액 삭제 - 실패")
    void testDeleteUserTotalPureAmountByUserId_Failure() {
        when(userTotalAmountRepository.findByUserUserId(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserTotalPureAmountException.class, () ->
                userTotalPureAmountService.deleteUserTotalPureAmountByUserId(1L));
    }
}
