package com.yes255.yes255booksusersserver.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.yes255.yes255booksusersserver.application.service.impl.UserServiceImpl;
import com.yes255.yes255booksusersserver.application.service.queue.producer.MessageProducer;
import com.yes255.yes255booksusersserver.common.exception.*;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.CouponAdaptor;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointLogRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointPolicyRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaProviderRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserAddressRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeLogRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserStateRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserTotalPureAmountRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.user.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private JpaUserGradeLogRepository userGradeLogRepository;

    @Mock
    private JpaUserAddressRepository userAddressRepository;

    @Mock
    private JpaPointPolicyRepository pointPolicyRepository;

    @Mock
    private JpaPointRepository pointRepository;

    @Mock
    private JpaPointLogRepository pointLogRepository;

    @Mock
    private JpaUserTotalPureAmountRepository totalAmountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CouponAdaptor couponAdaptor;

    @Mock
    private MessageProducer messageProducer;

    @Mock
    private InactiveStateService inactiveStateService;

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
        when(passwordEncoder.matches(request.password(), testUser.getUserPassword())).thenReturn(true);

        LoginUserResponse response = userService.findLoginUserByEmailByPassword(request);

        assertEquals(testUser.getUserId(), response.userId());
        assertEquals(testUser.getCustomer().getUserRole(), response.userRole());
        assertEquals(testUser.getUserState().getUserStateName(), response.loginStatusName());
    }

    @Test
    @DisplayName("이메일과 비밀번호로 로그인 - 실패 (사용자 없음)")
    void testFindLoginUserByEmailByPassword_UserNotFound() {

        String userEmail = "test@example.com";
        String password = "encodedPassword";
        LoginUserRequest request = LoginUserRequest.builder()
                .email(userEmail)
                .password(password)
                .build();

        when(userRepository.findByUserEmail(userEmail)).thenReturn(null);

        UserException exception = assertThrows(UserException.class,
                () -> userService.findLoginUserByEmailByPassword(request));

        assertEquals("회원이 존재 하지 않습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("이메일과 비밀번호로 로그인 - 실패 (비밀번호 불일치)")
    void testFindLoginUserByEmailByPassword_PasswordMismatch() {

        String userEmail = "test@example.com";
        String password = "wrongPassword";
        LoginUserRequest request = LoginUserRequest.builder()
                .email(userEmail)
                .password(password)
                .build();

        when(userRepository.findByUserEmail(userEmail)).thenReturn(testUser);
        when(passwordEncoder.matches(request.password(), testUser.getUserPassword())).thenReturn(false);

        UserException exception = assertThrows(UserException.class,
                () -> userService.findLoginUserByEmailByPassword(request));

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("이메일과 비밀번호로 로그인 - 실패 (탈퇴한 회원)")
    void testFindLoginUserByEmailByPassword_WithdrawnUser() {

        String userEmail = "withdrawn@example.com";
        String password = "encodedPassword";
        LoginUserRequest request = LoginUserRequest.builder()
                .email(userEmail)
                .password(password)
                .build();

        // 탈퇴한 회원을 설정
        User withdrawnUser = User.builder()
                .userState(UserState.builder().userStateName("WITHDRAWAL").build())
                .userPassword(password)
                .build();

        when(userRepository.findByUserEmail(userEmail)).thenReturn(withdrawnUser);
        when(passwordEncoder.matches(request.password(), withdrawnUser.getUserPassword())).thenReturn(true);

        UserException exception = assertThrows(UserException.class,
                () -> userService.findLoginUserByEmailByPassword(request));

        assertEquals("탈퇴한 회원입니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("이메일과 비밀번호로 로그인 - 실패 (휴면 회원)")
    void testFindLoginUserByEmailByPassword_InactiveUser() {

        String userEmail = "inactive@example.com";
        String password = "encodedPassword";
        LoginUserRequest request = LoginUserRequest.builder()
                .email(userEmail)
                .password(password)
                .build();

        // 휴면 상태의 회원 설정
        User inactiveUser = User.builder()
                .userState(UserState.builder().userStateName("INACTIVE").build())
                .userPassword(password)
                .build();

        when(userRepository.findByUserEmail(userEmail)).thenReturn(inactiveUser);
        when(passwordEncoder.matches(request.password(), inactiveUser.getUserPassword())).thenReturn(true);

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> userService.findLoginUserByEmailByPassword(request));

        assertEquals("회원이 휴면처리되었습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("이메일과 비밀번호로 로그인 - 실패 (3개월 이상 로그인하지 않은 회원)")
    void testFindLoginUserByEmailByPassword_InactiveAfterThreeMonths() {

        String userEmail = "inactiveAfterThreeMonths@example.com";
        String password = "encodedPassword";
        LoginUserRequest request = LoginUserRequest.builder()
                .email(userEmail)
                .password(password)
                .build();

        // 최근 로그인 날짜가 3개월 이상인 회원 설정
        User inactiveUser = User.builder()
                .userId(1L)
                .userLastLoginDate(LocalDateTime.now().minusMonths(4))
                .userPassword("encodedPassword")
                .userState(UserState.builder().userStateName("ACTIVE").build())
                .build();

        when(userRepository.findByUserEmail(userEmail)).thenReturn(inactiveUser);
        when(passwordEncoder.matches(request.password(), inactiveUser.getUserPassword())).thenReturn(true);

        doNothing().when(inactiveStateService).updateInActiveState(inactiveUser.getUserId());

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> userService.findLoginUserByEmailByPassword(request));

        assertEquals("회원이 휴면처리되었습니다.", exception.getErrorStatus().message());
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
    @DisplayName("특정 회원 조회 - 실패 (회원 없음)")
    void testFindUserByUserId_UserNotFound() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class,
                () -> userService.findUserByUserId(userId));

        assertEquals("회원이 존재 하지 않습니다.", exception.getErrorStatus().message());
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

        when(userRepository.findAllByUserNameAndUserPhone(userName, userPhone))
                .thenReturn(Collections.singletonList(testUser));

        List<FindUserResponse> responses = userService.findAllUserEmailByUserNameByUserPhone(request);

        assertFalse(responses.isEmpty());
        assertEquals(testUser.getUserEmail(), responses.getFirst().userEmail());
    }

    @Test
    @DisplayName("이름과 전화번호로 이메일 조회 - 실패 (회원 없음)")
    void testFindAllUserEmailByUserNameByUserPhone_UserNotFound() {

        String userName = "Nonexistent User";
        String userPhone = "010-0000-0000";
        FindEmailRequest request = FindEmailRequest.builder()
                .name(userName)
                .phone(userPhone)
                .build();

        when(userRepository.findAllByUserNameAndUserPhone(userName, userPhone))
                .thenReturn(null);

        UserException exception = assertThrows(UserException.class,
                () -> userService.findAllUserEmailByUserNameByUserPhone(request));

        assertEquals("회원이 존재 하지 않습니다.", exception.getErrorStatus().message());
    }


    @Test
    @DisplayName("회원 가입 - 성공")
    void testCreateUser() {
        // Given
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

        User testUser = User.builder()
                .userName(request.userName())
                .userBirth(request.userBirth())
                .userEmail(request.userEmail())
                .userPhone(request.userPhone())
                .provider(provider)
                .userState(userState)
                .userGrade(userGrade)
                .userPassword("encodedPassword")
                .build();

        Point testPoint = Point.builder()
                .pointCurrent(BigDecimal.ZERO)
                .user(testUser)
                .build();

        PointPolicy testPolicy = PointPolicy.builder()
                .pointPolicyName("SIGN-UP")
                .pointPolicyApplyAmount(BigDecimal.valueOf(1000))
                .pointPolicyCondition("SIGN-UP")
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(providerRepository.findByProviderName("LOCAL")).thenReturn(provider);
        when(userStateRepository.findByUserStateName("ACTIVE")).thenReturn(userState);
        when(userGradeRepository.findByUserGradeName("NORMAL")).thenReturn(userGrade);
        when(passwordEncoder.encode(request.userPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(pointPolicyRepository.findByPointPolicyName("SIGN-UP")).thenReturn(testPolicy);
        when(pointRepository.save(any(Point.class))).thenReturn(testPoint);


        // Stubbing behavior for messageProducer
        doNothing().when(messageProducer).sendWelcomeCouponMessage(testUser.getUserId());


        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertNotNull(response);
        assertEquals("Test User", response.userName());
        assertEquals("test@example.com", response.userEmail());
        assertEquals("010-1234-5678", response.userPhone());
        assertNotNull(response.userRegisterDate());
        assertNotNull(response.userPassword()); // Assuming you need to assert this
    }

    @Test
    @DisplayName("회원 가입 - 성공 (PAYCO)")
    void testCreateUserWithPAYCO() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("PAYCO User")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userEmail("payco@example.com")
                .userPhone("010-1234-5678")
                .providerName("PAYCO")
                .build();

        Customer customer = Customer.builder()
                .userRole("MEMBER")
                .build();

        Provider provider = Provider.builder()
                .providerId(1L)
                .providerName("PAYCO")
                .build();

        UserState userState = UserState.builder()
                .userStateId(1L)
                .userStateName("ACTIVE")
                .build();

        UserGrade userGrade = UserGrade.builder()
                .userGradeId(1L)
                .userGradeName("NORMAL")
                .build();

        User testUser = User.builder()
                .userName(request.userName())
                .userBirth(request.userBirth())
                .userEmail(request.userEmail())
                .userPhone(request.userPhone())
                .provider(provider)
                .userState(userState)
                .userGrade(userGrade)
                .userPassword("encodedPassword")
                .build();

        Point testPoint = Point.builder()
                .pointCurrent(BigDecimal.ZERO)
                .user(testUser)
                .build();

        PointPolicy testPolicy = PointPolicy.builder()
                .pointPolicyName("SIGN-UP")
                .pointPolicyApplyAmount(BigDecimal.valueOf(1000))
                .pointPolicyCondition("SIGN-UP")
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(providerRepository.findByProviderName("PAYCO")).thenReturn(provider);
        when(userStateRepository.findByUserStateName("ACTIVE")).thenReturn(userState);
        when(userGradeRepository.findByUserGradeName("NORMAL")).thenReturn(userGrade);
        when(passwordEncoder.encode(request.userPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(pointPolicyRepository.findByPointPolicyName("SIGN-UP")).thenReturn(testPolicy);
        when(pointRepository.save(any(Point.class))).thenReturn(testPoint);

        doNothing().when(messageProducer).sendWelcomeCouponMessage(testUser.getUserId());

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertNotNull(response);
        assertEquals("PAYCO User", response.userName());
        assertEquals("payco@example.com", response.userEmail());
        assertEquals("010-1234-5678", response.userPhone());
        assertNotNull(response.userRegisterDate());
        assertNotNull(response.userPassword());

        verify(pointPolicyRepository).findByPointPolicyName("SIGN-UP");
        verify(pointRepository).save(testPoint);
    }

    @Test
    @DisplayName("회원 가입 - 실패 (유효하지 않은 이메일)")
    void testCreateUserWithInvalidEmail() {

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            // Given
            userService.createUser(CreateUserRequest.builder()
                    .userName("Test User")
                    .userBirth(LocalDate.of(2000, 1, 1))
                    .userEmail("invalid-email") // 유효하지 않은 이메일
                    .userPhone("010-1234-5678")
                    .userPassword("password123")
                    .userConfirmPassword("password123")
                    .providerName("LOCAL")
                    .build());
        });

        assertEquals("유효한 이메일 형식이 아닙니다. yes255@shop.net 형식을 따라야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 - 실패 (유효하지 않은 전화번호)")
    void testCreateUserWithInvalidPhone() {

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            // Given
            userService.createUser(CreateUserRequest.builder()
                    .userName("Test User")
                    .userBirth(LocalDate.of(2000, 1, 1))
                    .userEmail("test@example.com")
                    .userPhone("012-345-6789") // 유효하지 않은 전화번호
                    .userPassword("password123")
                    .userConfirmPassword("password123")
                    .providerName("LOCAL")
                    .build());
        });

        assertEquals("유효한 전화번호 형식이 아닙니다. 010-1234-5678 형식을 따라야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 - 실패 (비밀번호 불일치)")
    void testCreateUser_PasswordMismatch() {

        CreateUserRequest request = CreateUserRequest.builder()
                .userName("Test User")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userPassword("password123")
                .userConfirmPassword("password456")
                .build();

        assertThrows(UserException.class,
                () -> userService.createUser(request),
                "비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("회원 가입 - 실패 (이미 가입된 이메일)")
    void testCreateUser_DuplicateEmail() {

        CreateUserRequest request = CreateUserRequest.builder()
                .userName("Test User")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userPassword("password123")
                .userConfirmPassword("password123")
                .build();

        when(userRepository.findByUserEmail(request.userEmail())).thenReturn(testUser);

        UserException exception = assertThrows(UserException.class,
                () -> userService.createUser(request));

        assertEquals("이미 사용중인 이메일입니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("회원 가입 - 실패 (제공자 정보 없음)")
    void testCreateUser_NoProviderInformation() {

        CreateUserRequest request = CreateUserRequest.builder()
                .userName("Test User")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userPassword("password123")
                .userConfirmPassword("password123")
                .build();

        when(providerRepository.findByProviderName("LOCAL")).thenReturn(null);

        ProviderException exception = assertThrows(ProviderException.class,
                () -> userService.createUser(request));

        assertEquals("제공자가 존재 하지 않습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("회원 가입 - 실패 (회원 상태 정보 없음)")
    void testCreateUser_NoUserStateInformation() {

        CreateUserRequest request = CreateUserRequest.builder()
                .userName("Test User")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userPassword("password123")
                .userConfirmPassword("password123")
                .build();

        when(providerRepository.findByProviderName(anyString())).thenReturn(Provider.builder().providerName("LOCAL").build());
        when(userStateRepository.findByUserStateName(anyString())).thenReturn(null);

        UserStateException exception = assertThrows(UserStateException.class,
                () -> userService.createUser(request));

        assertEquals("회원 상태가 존재 하지 않습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("회원 가입 - 실패 (회원 등급 정보 없음)")
    void testCreateUser_NoUserGradeInformation() {

        CreateUserRequest request = CreateUserRequest.builder()
                .userName("Test User")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userPassword("password123")
                .userConfirmPassword("password123")
                .build();

        when(providerRepository.findByProviderName(anyString())).thenReturn(Provider.builder().providerName("LOCAL").build());
        when(userStateRepository.findByUserStateName(anyString())).thenReturn(UserState.builder().userStateName("ACTIVE").build());
        when(userGradeRepository.findByUserGradeName("NORMAL")).thenReturn(null);

        UserGradeException exception = assertThrows(UserGradeException.class,
                () -> userService.createUser(request));

        assertEquals("회원 등급이 존재 하지 않습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("회원 정보 업데이트 - 성공")
    void testUpdateUser() {
        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .userName("Updated User")
                .userPhone("010-1234-5678")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userPassword("currentPassword123")
                .newUserPassword("newPassword123")
                .newUserConfirmPassword("newPassword123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword123", testUser.getUserPassword())).thenReturn(true);

        UpdateUserResponse response = userService.updateUser(userId, request);

        assertNotNull(response);
        assertEquals("Updated User", response.userName());
        assertEquals("010-1234-5678", response.userPhone());
        assertEquals(LocalDate.of(2000, 1, 1), response.userBirth());
    }

    @Test
    @DisplayName("회원 정보 수정 - 실패 (비밀번호 불일치)")
    void testUpdateUser_PasswordMismatch() {
        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .userName("Updated User")
                .userPhone("010-1234-5678")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userPassword("currentPassword123")
                .newUserPassword("newPassword123")
                .newUserConfirmPassword("differentPassword123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword123", testUser.getUserPassword())).thenReturn(true);

        UserException exception = assertThrows(UserException.class,
                () -> userService.updateUser(userId, request));

        assertEquals("새 비밀번호가 일치하지 않습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("회원 정보 수정 - 실패 (회원 없음)")
    void testUpdateUser_UserNotFound() {
        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .userName("Updated User")
                .userPhone("010-1234-5678")
                .userBirth(LocalDate.of(2000, 1, 1))
                .userPassword("currentPassword123")
                .newUserPassword("newPassword123")
                .newUserConfirmPassword("newPassword123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class,
                () -> userService.updateUser(userId, request));

        assertEquals("회원이 존재하지 않습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("회원 삭제 - 성공")
    void testDeleteUser() {
        Long userId = 1L;
        DeleteUserRequest request = DeleteUserRequest.builder()
                .userPassword("encodedPassword")
                .build();

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userStateRepository.findByUserStateName("WITHDRAWAL")).thenReturn(UserState.builder().userStateName("WITHDRAWAL").build());

        assertDoesNotThrow(() -> userService.deleteUser(userId, request));
    }

    @Test
    @DisplayName("회원 삭제 - 실패 (회원이 존재하지 않음)")
    void testDeleteUser_UserNotFound() {
        Long userId = 1L;
        DeleteUserRequest request = DeleteUserRequest.builder()
                .userPassword("encodedPassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> userService.deleteUser(userId, request));

        assertEquals("회원이 존재하지 않습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("회원 삭제 - 실패 (비밀번호 불일치)")
    void testDeleteUser_PasswordMismatch() {
        Long userId = 1L;
        DeleteUserRequest request = DeleteUserRequest.builder()
                .userPassword("wrongPassword")
                .build();

        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UserException exception = assertThrows(UserException.class, () -> userService.deleteUser(userId, request));

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getErrorStatus().message());
    }

    @Test
    @DisplayName("회원 삭제 - 실패 (회원 상태 존재하지 않음)")
    void testDeleteUser_UserStateNotFound() {
        Long userId = 1L;
        DeleteUserRequest request = DeleteUserRequest.builder()
                .userPassword("encodedPassword")
                .build();

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userStateRepository.findByUserStateName("WITHDRAWAL")).thenReturn(null);

        UserGradeException exception = assertThrows(UserGradeException.class, () -> userService.deleteUser(userId, request));

        assertEquals("회원 상태가 존재하지 않습니다.", exception.getErrorStatus().message());
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

    @Test
    @DisplayName("이메일 중복 확인 - 성공")
    void testIsEmailDuplicate_Exists() {
        String email = "test@example.com";
        when(userRepository.existsByUserEmail(email)).thenReturn(true);

        boolean result = userService.isEmailDuplicate(email);

        assertTrue(result);
        verify(userRepository).existsByUserEmail(email);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 실패 (존재하지 않는 이메일)")
    void testIsEmailDuplicate_NotExists() {
        String email = "test@example.com";
        when(userRepository.existsByUserEmail(email)).thenReturn(false);

        boolean result = userService.isEmailDuplicate(email);

        assertFalse(result);
        verify(userRepository).existsByUserEmail(email);
    }

    @Test
    @DisplayName("현재 월에 생일인 사용자 조회 - 성공")
    void testFindUserIdsWithBirthdaysInCurrentMonth() {
        int currentMonth = LocalDate.now().getMonthValue();
        when(userRepository.findUsersByBirthMonth(currentMonth)).thenReturn(Collections.singletonList(testUser));

        List<Long> result = userService.findUserIdsWithBirthdaysInCurrentMonth();

        assertEquals(1, result.size());
        assertEquals(testUser.getUserId(), result.getFirst());
        verify(userRepository).findUsersByBirthMonth(currentMonth);
    }

    @Test
    @DisplayName("현재 월에 생일인 사용자 조회 - 실패 (해당 사용자가 없음)")
    void testFindUserIdsWithBirthdaysInCurrentMonth_NoUsers() {
        int currentMonth = LocalDate.now().getMonthValue();
        when(userRepository.findUsersByBirthMonth(currentMonth)).thenReturn(Collections.emptyList());

        List<Long> result = userService.findUserIdsWithBirthdaysInCurrentMonth();

        assertTrue(result.isEmpty());
        verify(userRepository).findUsersByBirthMonth(currentMonth);
    }

    @Test
    @DisplayName("휴면 상태 해제 - 성공")
    void testUnLockDormantStateByEmail_UserExists() {
        UnlockDormantRequest request = new UnlockDormantRequest("test@example.com");
        UserState activeState = UserState.builder().userStateName("ACTIVE").build();

        when(userRepository.findByUserEmail(request.email())).thenReturn(testUser);
        when(userStateRepository.findByUserStateName("ACTIVE")).thenReturn(activeState);

        userService.unLockDormantStateByEmail(request);

        verify(userRepository).findByUserEmail(request.email());
        verify(userStateRepository).findByUserStateName("ACTIVE");
        assertNotNull(testUser.getUserLastLoginDate());
        assertEquals(activeState, testUser.getUserState());
    }

    @Test
    @DisplayName("회원 수정 페이지 진입 전 비밀번호 검증 - 성공")
    void testCheckUserPassword_ValidPassword() {
        CheckPasswordRequest validPasswordRequest = CheckPasswordRequest.builder()
                .password("encodedPassword")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(validPasswordRequest.password(), testUser.getUserPassword())).thenReturn(true);

        boolean result = userService.checkUserPassword(1L, validPasswordRequest);

        assertTrue(result);
    }


    @Test
    @DisplayName("회원 수정 페이지 진입 전 비밀번호 검증 - 실패 (비밀번호 불일치)")
    void testCheckUserPassword_InvalidPassword() {
        CheckPasswordRequest invalidPasswordRequest = CheckPasswordRequest.builder()
                .password("invalidPassword")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(invalidPasswordRequest.password(), testUser.getUserPassword())).thenReturn(false);

        boolean result = userService.checkUserPassword(1L, invalidPasswordRequest);

        assertFalse(result);
    }

    @Test
    @DisplayName("회원 수정 페이지 진입 전 비밀번호 검증 - 실패 (회원이 존재하지 않음)")
    void testCheckUserPassword_UserNotFound() {
        CheckPasswordRequest validPasswordRequest = CheckPasswordRequest.builder()
                .password("encodedPassword")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> {
            userService.checkUserPassword(1L, validPasswordRequest);
        });

        assertEquals("회원이 존재 하지 않습니다.", exception.getErrorStatus().message());
    }
}