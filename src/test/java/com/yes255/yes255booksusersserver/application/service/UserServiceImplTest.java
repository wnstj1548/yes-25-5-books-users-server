package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.UserServiceImpl;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.CouponAdaptor;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.user.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.FindUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.LoginUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UpdateUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaCustomerRepository customerRepository;

    @Mock
    private JpaProviderRepository providerRepository;

    @Mock
    private JpaUserGradeRepository userGradeRepository;

    @Mock
    private JpaUserStateRepository userStateRepository;

    @Mock
    private JpaCartRepository cartRepository;

    @Mock
    private JpaCartBookRepository cartBookRepository;

    @Mock
    private JpaUserAddressRepository userAddressRepository;

    @Mock
    private JpaPointPolicyRepository pointPolicyRepository;

    @Mock
    private JpaPointRepository pointRepository;

    @Mock
    private JpaPointLogRepository pointLogRepository;

    @Mock
    private JpaUserTotalAmountRepository totalAmountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CouponAdaptor couponAdaptor;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setup() {
        // Customer 설정
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
    }

    @Test
    @DisplayName("이메일과 비밀번호로 로그인 - 성공")
    void testFindLoginUserByEmailByPassword() {

        String userEmail = "test@example.com";
        String password = "encodedPassword";
        LoginUserRequest request = LoginUserRequest.builder()
                .email(userEmail)
                .password(password)
                .build();

        when(userRepository.findByUserEmail(userEmail)).thenReturn(testUser);
        when(passwordEncoder.matches(password, testUser.getUserPassword())).thenReturn(true);

        LoginUserResponse response = userService.findLoginUserByEmailByPassword(request);

        assertEquals(testUser.getUserId(), response.userId());
        assertEquals(testUser.getCustomer().getUserRole(), response.userRole());
        assertEquals(testUser.getUserState().getUserStateName(), response.loginStatusName());
    }

    @Test
    @DisplayName("특정 회원 조회 - 성공")
    void testFindUserByUserId() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.findUserByUserId(userId);

        assertEquals(testUser.getUserId(), response.userId());
        assertEquals(testUser.getUserName(), response.userName());
        assertEquals(testUser.getUserPhone(), response.userPhone());
        assertEquals(testUser.getUserEmail(), response.userEmail());
        assertEquals(testUser.getUserRegisterDate(), response.userRegisterDate());
        assertEquals(testUser.getUserLastLoginDate(), response.userLastLoginDate());
        assertEquals(testUser.getProvider().getProviderId(), response.providerId());
        assertEquals(testUser.getUserState().getUserStateId(), response.userStateId());
        assertEquals(testUser.getUserGrade().getUserGradeId(), response.userGradeId());
        assertEquals(testUser.getUserPassword(), response.userPassword());
    }

    @Test
    @DisplayName("이름과 전화번호로 이메일 조회 - 성공")
    void testFindAllUserEmailByUserNameByUserPhone() {

        String userName = "Test User";
        String userPhone = "010-1234-5678";
        FindEmailRequest request = FindEmailRequest.builder()
                .name(userName)
                .phone(userPhone)
                .build();

        when(userRepository.findAllByUserNameAndUserPhone(userName, userPhone, Pageable.unpaged()))
                .thenReturn(Collections.singletonList(testUser));

        List<FindUserResponse> responses = userService.findAllUserEmailByUserNameByUserPhone(request, Pageable.unpaged());

        assertFalse(responses.isEmpty());
        assertEquals(testUser.getUserEmail(), responses.getFirst().userEmail());
    }

    @Test
    @DisplayName("회원 가입 - 성공")
    void testCreateUser() {

        CreateUserRequest request = CreateUserRequest.builder()
                .userName("Test User")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userPassword("password123")
                .userConfirmPassword("password123")
                .build();

        Customer customer = Customer.builder()
                .userRole("MEMBER")
                .build();

        Provider provider = Provider.builder()
                .providerId(1L)
                .providerName("LOCAL")
                .build();

        UserState userState = UserState.builder()
                .userStateId(1L)
                .userStateName("ACTIVE")
                .build();

        UserGrade userGrade = UserGrade.builder()
                .userGradeId(1L)
                .userGradeName("NORMAL")
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(providerRepository.findByProviderName("LOCAL")).thenReturn(provider);
        when(userStateRepository.findByUserStateName("ACTIVE")).thenReturn(userState);
        when(userGradeRepository.findByUserGradeName("NORMAL")).thenReturn(userGrade);
        when(passwordEncoder.encode(request.userPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("Test User", response.userName());
        assertEquals("test@example.com", response.userEmail());
        assertEquals("010-1234-5678", response.userPhone());
        assertNotNull(response.userRegisterDate());
        assertNull(response.userLastLoginDate());
        assertEquals(provider.getProviderId(), response.providerId());
        assertEquals(userState.getUserStateId(), response.userStateId());
        assertEquals(userGrade.getUserGradeId(), response.userGradeId());
        assertNotNull(response.userPassword());
    }

    @Test
    @DisplayName("회원 정보 업데이트 - 성공")
    void testUpdateUser() {

        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .userName("Updated User")
                .userPhone("010-1234-5678")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userPassword("newPassword123")
                .userConfirmPassword("newPassword123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UpdateUserResponse response = userService.updateUser(userId, request);

        assertNotNull(response);
        assertEquals("Updated User", response.userName());
        assertEquals("010-1234-5678", response.userPhone());
        assertEquals(LocalDate.of(2000, 1, 1), response.userBirth());
    }

    @Test
    @DisplayName("회원 삭제 - 성공")
    void testDeleteUser() {

        Long userId = 1L;
        DeleteUserRequest request = DeleteUserRequest.builder()
                .password("encodedPassword")
                .build();

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> userService.deleteUser(userId, request));
    }

    @Test
    @DisplayName("마지막 로그인 일자 업데이트 - 성공")
    void testUpdateLastLoginDate() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> userService.updateLastLoginDate(userId));
    }

    @Test
    @DisplayName("이메일과 비밀번호로 로그인 - 성공")
    void testLoginUserByEmailByPassword() {

        String userEmail = "test@example.com";
        String password = "password123";
        LoginUserRequest request = LoginUserRequest.builder()
                .email(userEmail)
                .password(password)
                .build();
        

        when(userRepository.findByUserEmail(userEmail)).thenReturn(testUser);
        when(passwordEncoder.matches(password, testUser.getUserPassword())).thenReturn(true);

        boolean result = userService.loginUserByEmailByPassword(request);

        assertTrue(result);
    }

    @Test
    @DisplayName("이메일과 이름으로 비밀번호 찾기 - 성공")
    void testFindUserPasswordByEmailByName() {

        String userEmail = "test@example.com";
        String userName = "Test User";
        FindPasswordRequest request = FindPasswordRequest.builder()
                .email(userEmail)
                .name(userName)
                .build();

        when(userRepository.findByUserEmailAndUserName(userEmail, userName)).thenReturn(testUser);

        boolean result = userService.findUserPasswordByEmailByName(request);

        assertTrue(result);
    }

    @Test
    @DisplayName("사용자 비밀번호 업데이트 - 성공")
    void testSetUserPasswordByUserId() {

        Long userId = 1L;
        String newPassword = "newPassword123";
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .password(newPassword)
                .confirmPassword(newPassword)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        boolean result = userService.setUserPasswordByUserId(userId, request);

        assertTrue(result);
    }
}
