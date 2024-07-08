package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.CouponUserServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.CouponUserException;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.CouponAdaptor;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCouponUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.couponuser.UpdateCouponRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.CouponBoxResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.CouponInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.ExpiredCouponUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @InjectMocks
    private CouponUserServiceImpl couponUserService;

    private User testUser;
    private CouponUser testCouponUser;

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

        CouponUserException exception = assertThrows(CouponUserException.class, () -> couponUserService.createCouponUser(1L, 1L));
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

        CouponUserException exception = assertThrows(CouponUserException.class, () -> couponUserService.getUserCoupons(1L, PageRequest.of(0, 10)));
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
    @DisplayName("쿠폰 상태 업데이트 - (실패) 사용자 쿠폰을 찾을 수 없음")
    void testUpdateCouponState_UserCouponNotFound() {
        when(couponUserRepository.findByUserCouponIdAndUserUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CouponUserException exception = assertThrows(CouponUserException.class, () -> couponUserService.updateCouponState(1L, new UpdateCouponRequest(1L, "use")));
    }

    @Test
    @DisplayName("쿠폰 상태 업데이트 - 성공")
    void testUpdateCouponState_Success() {
        when(couponUserRepository.findByUserCouponIdAndUserUserId(anyLong(), anyLong())).thenReturn(Optional.of(testCouponUser));

        couponUserService.updateCouponState(1L, new UpdateCouponRequest(1L, "use"));

        assertThat(testCouponUser.getUserCouponStatus()).isEqualTo(CouponUser.UserCouponStatus.USED);
        verify(couponUserRepository, times(1)).save(testCouponUser);
    }

    @Test
    @DisplayName("만료된 쿠폰 확인 - 실패 (쿠폰이 없음)")
    void testCheckExpiredCoupon_NoCouponsFound() {
        when(couponUserRepository.findByUserCouponStatus(any())).thenReturn(Collections.emptyList());

        CouponUserException exception = assertThrows(CouponUserException.class, () -> couponUserService.checkExpiredCoupon());
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
}
