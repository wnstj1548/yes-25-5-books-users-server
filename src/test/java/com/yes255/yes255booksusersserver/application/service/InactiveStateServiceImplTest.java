package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.InactiveStateServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InactiveStateServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaUserStateRepository userStateRepository;

    @InjectMocks
    private InactiveStateServiceImpl inactiveStateService;

    private User testUser;
    private UserState inactiveState;

    @BeforeEach
    void setUp() {

        Customer testCustomer = Customer.builder()
                .userId(1L)
                .userRole("USER")
                .build();

        testUser = User.builder()
                .customer(testCustomer)
                .userName("Test User")
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userRegisterDate(LocalDateTime.now().minusDays(1))
                .userLastLoginDate(LocalDateTime.now())
                .provider(Provider.builder().providerName("LOCAL").build())
                .userState(UserState.builder().userStateName("ACTIVE").build())
                .userGrade(UserGrade.builder().userGradeName("NORMAL").build())
                .userPassword("encodedPassword")
                .build();

        inactiveState = UserState.builder()
                .userStateId(1L)
                .userStateName("INACTIVE")
                .build();
    }

    @Test
    @DisplayName("회원 상태를 휴면으로 업데이트 - 성공")
    void testUpdateInActiveState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userStateRepository.findByUserStateName("INACTIVE")).thenReturn(inactiveState);

        inactiveStateService.updateInActiveState(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userStateRepository, times(1)).findByUserStateName("INACTIVE");
    }

    @Test
    @DisplayName("회원 상태를 휴면으로 업데이트 - 실패 (회원을 찾을 수 없음)")
    void testUpdateInActiveState_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> {
            inactiveStateService.updateInActiveState(1L);
        });

        verify(userRepository, times(1)).findById(1L);
        verify(userStateRepository, never()).findByUserStateName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }


}
