package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.CartServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.CartException;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCartRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private JpaCartRepository cartRepository;

    @Mock
    private JpaCartBookRepository cartBookRepository;

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;

    @BeforeEach
    void setup() {
        // Customer 설정
        Customer testCustomer = Customer.builder()
                .userId(1L)
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

        // UserGrade 설정
        UserGrade testUserGrade = UserGrade.builder()
                .userGradeId(1L)
                .userGradeName("Gold")
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
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void testDeleteByUserId_Success() {

        Long userId = 1L;
        Cart cart = new Cart(1L, LocalDate.now(), testUser);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser_UserId(userId)).thenReturn(cart);

        assertDoesNotThrow(() -> cartService.deleteByUserId(userId));
    }

    @Test
    @DisplayName("사용자 삭제 - 사용자가 존재하지 않는 경우")
    void testDeleteByUserId_UserNotFound() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(CartException.class, () -> cartService.deleteByUserId(userId));
    }
}
