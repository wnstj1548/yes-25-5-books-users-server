package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.CouponUserServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.CouponUserException;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.CouponAdaptor;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCouponUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.couponuser.UpdateCouponRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.*;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponUserServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaCouponUserRepository couponUserRepository;

    @Mock
    private CouponAdaptor couponAdaptor;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CouponUserServiceImpl couponUserService;

    private User testUser;
    private CouponUser testCouponUser;
    private CouponInfoResponse testCouponInfoResponse;

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

        Date pastDate = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();

        testCouponUser = CouponUser.builder()
                .user(testUser)
                .couponId(1L)
                .userCouponType("일반")
                .userCouponStatus(CouponUser.UserCouponStatus.ACTIVE)
                .couponExpiredAt(pastDate)
                .build();

        testCouponInfoResponse = CouponInfoResponse.builder()
                .couponId(1L)
                .couponName("테스트 쿠폰")
                .couponMinAmount(BigDecimal.ZERO)
                .couponMaxAmount(BigDecimal.TEN)
                .couponDiscountRate(BigDecimal.ONE)
                .couponDiscountAmount(BigDecimal.ONE)
                .couponCreatedAt(new Date())
                .couponCode("CODE123")
                .build();
    }

    @Test
    @DisplayName("쿠폰 사용자 생성 - 실패 (사용자를 찾을 수 없음)")
    void testCreateCouponUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> couponUserService.createCouponUser(1L, 1L));
    }

    @Test
    @DisplayName("쿠폰 사용자 생성 - 실패 (중복 쿠폰)")
    void testCreateCouponUser_DuplicateCoupon() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(couponUserRepository.existsByCouponIdAndUser(anyLong(), any())).thenReturn(true);

        assertThrows(CouponUserException.class, () -> couponUserService.createCouponUser(1L, 1L));
    }

    @Test
    @DisplayName("쿠폰 사용자 생성 - 성공")
    void testCreateCouponUser_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(couponUserRepository.existsByCouponIdAndUser(anyLong(), any())).thenReturn(false);
        when(couponAdaptor.getCouponExpiredDate(anyLong())).thenReturn(new ExpiredCouponUserResponse(new Date()));

        couponUserService.createCouponUser(1L, 1L);

        verify(couponUserRepository, times(1)).save(any(CouponUser.class));
    }

    @Test
    @DisplayName("사용자 쿠폰 가져오기 - 실패 (사용자를 찾을 수 없음)")
    void testGetUserCoupons_UserNotFound() {
        when(couponUserRepository.findByUserUserId(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(CouponUserException.class, () -> couponUserService.getUserCoupons(1L, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("사용자 쿠폰 가져오기 - 성공")
    void testGetUserCoupons_Success() {
        List<CouponUser> couponUsers = Collections.singletonList(testCouponUser);
        when(couponUserRepository.findByUserUserId(anyLong())).thenReturn(couponUsers);
        when(couponAdaptor.getCouponsInfo(anyList())).thenReturn(Collections.singletonList(
                CouponInfoResponse.builder()
                        .couponId(1L)
                        .couponName("테스트 쿠폰")
                        .couponMinAmount(BigDecimal.ZERO)
                        .couponMaxAmount(BigDecimal.TEN)
                        .couponDiscountRate(BigDecimal.ONE)
                        .couponDiscountAmount(BigDecimal.ONE)
                        .couponCreatedAt(new Date())
                        .couponCode("CODE123")
                        .build()
        ));

        Page<CouponBoxResponse> response = couponUserService.getUserCoupons(1L, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("쿠폰 상태 업데이트 - 실패 (사용자 쿠폰을 찾을 수 없음)")
    void testUpdateCouponState_UserCouponNotFound() {
        when(couponUserRepository.findByUserCouponIdAndUserUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(CouponUserException.class, () -> couponUserService.updateCouponState(1L,
            List.of(new UpdateCouponRequest(1L, "use"))));
    }

    @Test
    @DisplayName("쿠폰 상태 업데이트 - 성공 (사용)")
    void testUpdateCouponState_Success() {
        when(couponUserRepository.findByUserCouponIdAndUserUserId(anyLong(), anyLong())).thenReturn(Optional.of(testCouponUser));

        couponUserService.updateCouponState(1L, List.of(new UpdateCouponRequest(1L, "use")));

        assertThat(testCouponUser.getUserCouponStatus()).isEqualTo(CouponUser.UserCouponStatus.USED);
    }

    @Test
    @DisplayName("쿠폰 상태 업데이트 - 성공 (롤백)")
    void testUpdateCouponState_Success_Rollback() {
        when(couponUserRepository.findByUserCouponIdAndUserUserId(anyLong(), anyLong())).thenReturn(Optional.of(testCouponUser));

        couponUserService.updateCouponState(1L, List.of(new UpdateCouponRequest(1L, "rollback")));

        assertThat(testCouponUser.getUserCouponStatus()).isEqualTo(CouponUser.UserCouponStatus.ACTIVE);
        assertThat(testCouponUser.getUserCouponUsedAt()).isNull();
    }

    @Test
    @DisplayName("만료된 쿠폰 확인 - 실패 (쿠폰이 없음)")
    void testCheckExpiredCoupon_NoCouponsFound() {
        when(couponUserRepository.findByUserCouponStatus(any())).thenReturn(Collections.emptyList());

        assertThrows(CouponUserException.class, () -> couponUserService.checkExpiredCoupon());
    }

    @Test
    @DisplayName("만료된 쿠폰 확인 - 성공")
    void testCheckExpiredCoupon_Success() {
        when(couponUserRepository.findByUserCouponStatus(any())).thenReturn(Collections.singletonList(testCouponUser));

        couponUserService.checkExpiredCoupon();

        assertThat(testCouponUser.getUserCouponStatus()).isEqualTo(CouponUser.UserCouponStatus.EXPIRED);
        verify(couponUserRepository, times(1)).save(testCouponUser);
    }

    @Test
    @DisplayName("만료된 쿠폰 삭제 - 성공")
    void testDeleteExpiredCoupons_Success() {
        List<CouponUser> expiredCoupons = Collections.singletonList(testCouponUser);
        when(couponUserRepository.findByUserCouponStatusAndCouponExpiredAtBefore(any(), any())).thenReturn(expiredCoupons);

        couponUserService.deleteExpiredCoupons();

        verify(couponUserRepository, times(1)).deleteAll(expiredCoupons);
    }

    @Test
    @DisplayName("쿠폰 상태에 따른 회원 쿠폰 목록 조회 - 성공")
    void testGetStateUserCoupons_Success() {
        Page<CouponUser> couponUserPage = new PageImpl<>(Collections.singletonList(testCouponUser), PageRequest.of(0, 10), 1);
        List<CouponInfoResponse> couponInfoResponseList = Collections.singletonList(testCouponInfoResponse);

        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any(), any())).thenReturn(couponUserPage);
        when(couponAdaptor.getCouponsInfo(anyList())).thenReturn(couponInfoResponseList);

        Page<CouponBoxResponse> response = couponUserService.getStateUserCoupons(1L, CouponUser.UserCouponStatus.ACTIVE, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().getFirst().couponName()).isEqualTo("테스트 쿠폰");
    }

    @Test
    @DisplayName("쿠폰 상태에 따른 회원 쿠폰 목록 조회 - 실패 (쿠폰이 없음)")
    void testGetStateUserCoupons_NoCoupons() {
        Page<CouponUser> emptyCouponUserPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any(), any())).thenReturn(emptyCouponUserPage);

        Page<CouponBoxResponse> response = couponUserService.getStateUserCoupons(1L, CouponUser.UserCouponStatus.ACTIVE, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 회원의 모든 쿠폰 목록 조회 - 성공")
    void testGetAllUserCouponsByUserId_Success() {
        List<CouponUser> couponUsers = Collections.singletonList(testCouponUser);
        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any())).thenReturn(couponUsers);
        when(couponAdaptor.getCouponsInfo(anyList())).thenReturn(Collections.singletonList(
                CouponInfoResponse.builder()
                        .couponId(1L)
                        .couponName("테스트 쿠폰")
                        .couponMinAmount(BigDecimal.ZERO)
                        .couponDiscountAmount(BigDecimal.ONE)
                        .couponDiscountRate(BigDecimal.ONE)
                        .build()
        ));

        List<ReadUserCouponResponse> response = couponUserService.getAllUserCouponsByUserId(1L);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().couponId()).isEqualTo(1L);

        verify(couponUserRepository, times(1)).findByUserUserIdAndUserCouponStatus(1L, CouponUser.UserCouponStatus.ACTIVE);
    }

    @Test
    @DisplayName("특정 회원의 모든 쿠폰 목록 조회 - 실패 (쿠폰이 없음)")
    void testGetAllUserCouponsByUserId_NoCouponsFound() {
        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any())).thenReturn(Collections.emptyList());

        List<ReadUserCouponResponse> response = couponUserService.getAllUserCouponsByUserId(1L);

        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("회원의 가장 할인 금액이 높은 쿠폰 반환 - 실패 (쿠폰이 없음)")
    void testGetMaximumDiscountCouponByUserId_NoCouponsFound() {
        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any())).thenReturn(Collections.emptyList());

        ReadMaximumDiscountCouponResponse response = couponUserService.getMaximumDiscountCouponByUserId(1L, 100);

        assertThat(response.couponId()).isNull();
        assertThat(response.discountAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("회원의 가장 할인 금액이 높은 쿠폰 반환 - 정액 할인 적용")
    void testGetMaximumDiscountCouponByUserId_FixedDiscount() {
        testCouponInfoResponse = CouponInfoResponse.builder()
                .couponId(1L)
                .couponName("테스트 쿠폰")
                .couponMinAmount(BigDecimal.ZERO)
                .couponDiscountAmount(BigDecimal.TEN)
                .couponDiscountRate(BigDecimal.TEN)
                .couponMaxAmount(BigDecimal.TEN)
                .couponDiscountType(false)
                .build();

        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(testCouponUser));
        when(couponAdaptor.getCouponsInfo(anyList()))
                .thenReturn(Collections.singletonList(testCouponInfoResponse));

        ReadMaximumDiscountCouponResponse response = couponUserService.getMaximumDiscountCouponByUserId(1L, 100);

        assertThat(response.couponId()).isEqualTo(1L);
        assertThat(response.discountAmount()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("회원의 가장 할인 금액이 높은 쿠폰 반환 - 정율 할인 적용")
    void testGetMaximumDiscountCouponByUserId_PercentageDiscount() {
        testCouponInfoResponse = CouponInfoResponse.builder()
                .couponId(1L)
                .couponName("테스트 쿠폰")
                .couponDiscountRate(BigDecimal.TEN) // 10% 할인
                .couponDiscountAmount(BigDecimal.TEN)
                .couponDiscountType(true) // 정율 할인
                .build();

        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(testCouponUser));
        when(couponAdaptor.getCouponsInfo(anyList()))
                .thenReturn(Collections.singletonList(testCouponInfoResponse));

        ReadMaximumDiscountCouponResponse response = couponUserService.getMaximumDiscountCouponByUserId(1L, 100);

        assertThat(response.couponId()).isEqualTo(1L);
        assertThat(response.discountAmount()).isEqualTo(BigDecimal.valueOf(10.0)); // 100 * 10% = 10
    }

    @Test
    @DisplayName("회원의 가장 할인 금액이 높은 쿠폰 반환 - 최대 할인 금액 제한 적용")
    void testGetMaximumDiscountCouponByUserId_MaxDiscountAmount() {
        testCouponInfoResponse = CouponInfoResponse.builder()
                .couponId(1L)
                .couponName("테스트 쿠폰")
                .couponDiscountType(true) // 정율 할인
                .couponDiscountAmount(BigDecimal.TEN)
                .couponDiscountRate(BigDecimal.valueOf(50)) // 50% 할인
                .couponMaxAmount(BigDecimal.valueOf(20)) // 최대 할인 금액 20
                .build();

        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(testCouponUser));
        when(couponAdaptor.getCouponsInfo(anyList()))
                .thenReturn(Collections.singletonList(testCouponInfoResponse));

        ReadMaximumDiscountCouponResponse response = couponUserService.getMaximumDiscountCouponByUserId(1L, 100);

        assertThat(response.couponId()).isEqualTo(1L);
        assertThat(response.discountAmount()).isEqualTo(BigDecimal.valueOf(20)); // 최대 할인 금액 20 적용
    }

    @Test
    @DisplayName("회원의 가장 할인 금액이 높은 쿠폰 반환 - 최소 주문 금액 조건 미충족")
    void testGetMaximumDiscountCouponByUserId_MinOrderAmountNotMet() {
        testCouponInfoResponse = CouponInfoResponse.builder()
                .couponId(1L)
                .couponName("테스트 쿠폰")
                .couponDiscountType(true)
                .couponDiscountRate(BigDecimal.valueOf(10))
                .couponMinAmount(BigDecimal.valueOf(200)) // 최소 주문 금액 200
                .build();

        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(testCouponUser));
        when(couponAdaptor.getCouponsInfo(anyList()))
                .thenReturn(Collections.singletonList(testCouponInfoResponse));

        ReadMaximumDiscountCouponResponse response = couponUserService.getMaximumDiscountCouponByUserId(1L, 100);

        assertThat(response.couponId()).isNull();
        assertThat(response.discountAmount()).isEqualTo(BigDecimal.ZERO); // 최소 주문 금액 조건 미충족
    }

    @Test
    @DisplayName("회원의 가장 할인 금액이 높은 쿠폰 반환 - 복잡한 조건 적용")
    void testGetMaximumDiscountCouponByUserId_ComplexConditions() {
        testCouponInfoResponse = CouponInfoResponse.builder()
                .couponId(1L)
                .couponName("테스트 쿠폰")
                .couponDiscountType(true)
                .couponDiscountRate(BigDecimal.valueOf(30)) // 30% 할인
                .couponMinAmount(BigDecimal.valueOf(50)) // 최소 주문 금액 50
                .couponMaxAmount(BigDecimal.valueOf(25)) // 최대 할인 금액 25
                .build();

        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(testCouponUser));
        when(couponAdaptor.getCouponsInfo(anyList()))
                .thenReturn(Collections.singletonList(testCouponInfoResponse));

        ReadMaximumDiscountCouponResponse response = couponUserService.getMaximumDiscountCouponByUserId(1L, 100);

        assertThat(response.couponId()).isEqualTo(1L);
        assertThat(response.discountAmount()).isEqualTo(BigDecimal.valueOf(25)); // 100 * 30% = 30, 최대 할인 금액 25 적용
    }

    @Test
    @DisplayName("회원의 가장 할인 금액이 높은 쿠폰 반환 - 정율 할인, 최대 할인 금액 제한 미적용")
    void testGetMaximumDiscountCouponByUserId_NoMaxDiscountAmount() {
        testCouponInfoResponse = CouponInfoResponse.builder()
                .couponId(1L)
                .couponName("테스트 쿠폰")
                .couponDiscountType(true)
                .couponDiscountRate(BigDecimal.valueOf(20)) // 20% 할인
                .couponMinAmount(BigDecimal.valueOf(50)) // 최소 주문 금액 50
                .couponMaxAmount(null)
                .build();

        when(couponUserRepository.findByUserUserIdAndUserCouponStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(testCouponUser));
        when(couponAdaptor.getCouponsInfo(anyList()))
                .thenReturn(Collections.singletonList(testCouponInfoResponse));

        ReadMaximumDiscountCouponResponse response = couponUserService.getMaximumDiscountCouponByUserId(1L, 250);

        assertThat(response.couponId()).isEqualTo(1L);
        assertThat(response.discountAmount()).isEqualTo(BigDecimal.valueOf(50.0)); // 250 * 20% = 50
    }

    @Test
    @DisplayName("생일 쿠폰 생성")
    void testCreateCouponUserForBirthday() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        couponUserService.createCouponUserForBirthday(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(couponUserRepository, times(1)).save(any());
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("웰컴 쿠폰 생성")
    void testCreateCouponUserForWelcome() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        couponUserService.createCouponUserForWelcome(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(couponUserRepository, times(1)).save(any(CouponUser.class));
    }

    @Test
    @DisplayName("생일 쿠폰 생성 - 회원이 존재하지 않는 경우")
    void testCreateCouponUserForBirthday_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            couponUserService.createCouponUserForBirthday(1L);
        } catch (UserException e) {
            verify(userRepository, times(1)).findById(1L);
            verify(couponUserRepository, never()).save(any(CouponUser.class));
        }
    }

    @Test
    @DisplayName("웰컴 쿠폰 생성 - 회원이 존재하지 않는 경우")
    void testCreateCouponUserForWelcome_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            couponUserService.createCouponUserForWelcome(1L);
        } catch (UserException e) {
            verify(userRepository, times(1)).findById(1L);
            verify(couponUserRepository, never()).save(any(CouponUser.class));
        }
    }

    @Test
    @DisplayName("쿠폰 만료일 계산")
    void testCalculateExpiryDate() {
        int validDays = 30;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, validDays);
        Date expectedExpiryDate = calendar.getTime();

        Date actualExpiryDate = couponUserService.calculateExpiryDate(validDays);

        // 허용 범위를 1밀리초로 설정하여 비교
        long difference = Math.abs(expectedExpiryDate.getTime() - actualExpiryDate.getTime());
        assertTrue(difference <= 1, "Dates should be within 1 millisecond difference");
    }

    @Test
    @DisplayName("생일 쿠폰 만료일 계산")
    void testCalculateBirthdayCouponExpiryDate() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        Date expectedExpiryDate = Date.from(lastDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        couponUserService.createCouponUserForBirthday(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(couponUserRepository, times(1)).save(argThat(couponUser ->
                couponUser.getCouponExpiredAt().equals(expectedExpiryDate)));
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(anyString(), anyString(), eq(31L), eq(TimeUnit.DAYS));
    }
}
