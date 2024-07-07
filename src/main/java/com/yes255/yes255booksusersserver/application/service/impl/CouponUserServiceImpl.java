package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import com.yes255.yes255booksusersserver.common.exception.CouponUserException;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.CouponAdaptor;
import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCouponUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.couponuser.ReadMaximumDiscountCouponRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.couponuser.UpdateCouponRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CouponUserServiceImpl implements CouponUserService {

    private final JpaUserRepository userRepository;
    private final JpaCouponUserRepository couponUserRepository;
    private final CouponAdaptor couponAdaptor;

    @Override
    public void createCouponUser(Long couponId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

        // 중복 쿠폰 여부 확인
        if (couponUserRepository.existsByCouponIdAndUser(couponId, user)) {
            throw new CouponUserException(ErrorStatus.toErrorStatus("회원 쿠폰이 이미 존재합니다.", 400, LocalDateTime.now()));
        }

        ExpiredCouponUserResponse couponUserResponse = couponAdaptor.getCouponExpiredDate(couponId);

        couponUserRepository.save(CouponUser.builder()
                        .userCouponType("일반")
                        .userCouponStatus(CouponUser.UserCouponStatus.ACTIVE)
                        .couponExpiredAt(couponUserResponse.couponExpiredAt())
                        .couponId(couponId)
                        .user(user)
                        .build());
    }

    // 회원의 모든 쿠폰 목록 조회
    @Transactional(readOnly = true)
    @Override
    public Page<CouponBoxResponse> getUserCoupons(Long userId, Pageable pageable) {

        // 회원 쿠폰 리스트 가져오기
        List<CouponUser> couponUsers = couponUserRepository.findByUserUserId(userId);

        if (couponUsers.isEmpty()) {
            throw new CouponUserException(ErrorStatus.toErrorStatus("회원 쿠폰이 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        List<Long> couponIds = couponUsers.stream()
                .map(CouponUser::getCouponId)
                .collect(Collectors.toList());

        List<CouponInfoResponse> couponInfoResponses = couponAdaptor.getCouponsInfo(couponIds);

        // CouponInfoResponse를 매핑하기 위한 Map 생성
        Map<Long, CouponInfoResponse> couponInfoMap = couponInfoResponses.stream()
                .collect(Collectors.toMap(CouponInfoResponse::couponId, Function.identity()));

        // CouponBoxResponse로 변환 및 만료일자 기준으로 정렬
        List<CouponBoxResponse> couponBoxResponses = couponUsers.stream()
                .sorted(Comparator.comparing(CouponUser::getCouponExpiredAt))
                .map(couponUser -> {
                    CouponInfoResponse couponInfoResponse = couponInfoMap.get(couponUser.getCouponId());
                    return CouponBoxResponse.builder()
                            .userCouponId(couponUser.getUserCouponId())
                            .userCouponUsedAt(couponUser.getUserCouponUsedAt())
                            .userCouponStatus(couponUser.getUserCouponStatus())
                            .userCouponType(couponUser.getUserCouponType())
                            .CouponExpiredAt(couponUser.getCouponExpiredAt())
                            .couponId(couponUser.getCouponId())
                            .userId(couponUser.getUser().getUserId())
                            .couponName(couponInfoResponse.couponName())
                            .couponMinAmount(couponInfoResponse.couponMinAmount())
                            .couponMaxAmount(couponInfoResponse.couponMaxAmount())
                            .couponDiscountAmount(couponInfoResponse.couponDiscountAmount())
                            .couponDiscountRate(couponInfoResponse.couponDiscountRate())
                            .couponCreatedAt(couponInfoResponse.couponCreatedAt())
                            .couponCode(couponInfoResponse.couponCode())
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(couponBoxResponses, pageable, couponBoxResponses.size());
    }

    // 쿠폰 상태에 따른 회원 쿠폰 목록 조회 (사용 가능한 쿠폰, 사용한 쿠폰, 만료된 쿠폰)
    @Transactional(readOnly = true)
    @Override
    public Page<CouponBoxResponse> getStateUserCoupons(Long userId, CouponUser.UserCouponStatus userCouponStatus, Pageable pageable) {

        // 회원의 사용된 쿠폰 리스트 가져오기
        List<CouponUser> couponUsers = couponUserRepository.findByUserUserIdAndUserCouponStatus(userId, userCouponStatus);

        List<Long> couponIds = couponUsers.stream()
                .map(CouponUser::getCouponId)
                .collect(Collectors.toList());

        List<CouponInfoResponse> couponInfoResponses = couponAdaptor.getCouponsInfo(couponIds);

        // CouponInfoResponse를 매핑하기 위한 Map 생성
        Map<Long, CouponInfoResponse> couponInfoMap = couponInfoResponses.stream()
                .collect(Collectors.toMap(CouponInfoResponse::couponId, Function.identity()));

        // CouponBoxResponse로 변환 및 만료일자 기준으로 정렬
        List<CouponBoxResponse> couponBoxResponses = couponUsers.stream()
                .sorted(Comparator.comparing(CouponUser::getCouponExpiredAt))
                .map(couponUser -> {
                    CouponInfoResponse couponInfoResponse = couponInfoMap.get(couponUser.getCouponId());
                    return CouponBoxResponse.builder()
                            .userCouponId(couponUser.getUserCouponId())
                            .userCouponUsedAt(couponUser.getUserCouponUsedAt())
                            .userCouponStatus(couponUser.getUserCouponStatus())
                            .userCouponType(couponUser.getUserCouponType())
                            .couponId(couponUser.getCouponId())
                            .CouponExpiredAt(couponUser.getCouponExpiredAt())
                            .userId(couponUser.getUser().getUserId())
                            .couponName(couponInfoResponse.couponName())
                            .couponMinAmount(couponInfoResponse.couponMinAmount())
                            .couponMaxAmount(couponInfoResponse.couponMaxAmount())
                            .couponDiscountAmount(couponInfoResponse.couponDiscountAmount())
                            .couponDiscountRate(couponInfoResponse.couponDiscountRate())
                            .couponCreatedAt(couponInfoResponse.couponCreatedAt())
                            .couponCode(couponInfoResponse.couponCode())
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(couponBoxResponses, pageable, couponBoxResponses.size());
    }

    // 주문 서버로부터 쿠폰 사용 처리
    @Override
    public void updateCouponState(Long userId, UpdateCouponRequest couponRequest) {

        CouponUser couponUser = couponUserRepository.findByUserCouponIdAndUserUserId(couponRequest.userCouponId(), userId)
                .orElseThrow(() -> new CouponUserException(ErrorStatus.toErrorStatus("회원 쿠폰이 존재하지 않습니다.", 400, LocalDateTime.now())));

        if (couponRequest.operationType().equals("use")) {
            couponUser.updateUserCouponStatus(CouponUser.UserCouponStatus.USED);
            couponUser.updateCouponUsedAt(LocalDate.now());
        }
        else if (couponRequest.operationType().equals("rollback")) {
            couponUser.updateUserCouponStatus(CouponUser.UserCouponStatus.ACTIVE);
            couponUser.updateCouponUsedAt(null);
        }

        couponUserRepository.save(couponUser);
    }

    // 매일 자정에 쿠폰 만료 여부 체크
    @Transactional
    @Override
    public void checkExpiredCoupon() {

        // 회원의 사용된 쿠폰 리스트 가져오기
        List<CouponUser> couponUsers = couponUserRepository.findByUserCouponStatus(CouponUser.UserCouponStatus.ACTIVE);

        if (couponUsers.isEmpty()) {
            throw new CouponUserException(ErrorStatus.toErrorStatus("회원 쿠폰이 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        Date today = new Date();

        for (CouponUser couponUser : couponUsers) {
            if (couponUser.getCouponExpiredAt().before(today)) {
                couponUser.updateUserCouponStatus(CouponUser.UserCouponStatus.EXPIRED);
                couponUserRepository.save(couponUser);
            }
        }
    }

    // 매일 자정에 한 달 지난 만료된 쿠폰 삭제
    @Transactional
    @Override
    public void deleteExpiredCoupons() {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(today);
        cal.add(Calendar.MONTH, -1); // 한 달 전

        Date oneMonthAgo = cal.getTime();

        List<CouponUser> expiredCoupons = couponUserRepository.findByUserCouponStatusAndCouponExpiredAtBefore(CouponUser.UserCouponStatus.EXPIRED, oneMonthAgo);

        couponUserRepository.deleteAll(expiredCoupons);
    }

    @Override
    @Transactional
    public void createCouponUserForBirthday(Long userId) {
        Long birthdayCouponPolicyId = 2L;
        log.info("Creating birthday coupon for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now()));
                });

        log.info("Found user: {}", user);

        // 이번 달의 첫 날과 마지막 날 계산
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        Date couponExpiredAt = Date.from(lastDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        log.info("Calculated coupon expiry date: {}", couponExpiredAt);

        couponUserRepository.save(CouponUser.builder()
                .userCouponType("생일")
                .userCouponStatus(CouponUser.UserCouponStatus.ACTIVE)
                .couponExpiredAt(couponExpiredAt)
                .couponId(birthdayCouponPolicyId)
                .user(user)
                .build());

        log.info("Birthday coupon created for user: {}", userId);
    }

    @Override
    @Transactional
    public void createCouponUserForWelcome(Long userId) {
        Long welcomeCouponPolicyId = 1L;
        int welcomeCouponValidDays = 30;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

        Date couponExpiredAt = calculateExpiryDate(welcomeCouponValidDays);

        couponUserRepository.save(CouponUser.builder()
                .userCouponType("웰컴")
                .userCouponStatus(CouponUser.UserCouponStatus.ACTIVE)
                .couponExpiredAt(couponExpiredAt)
                .couponId(welcomeCouponPolicyId)
                .user(user)
                .build());
    }

    private Date calculateExpiryDate(int validDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, validDays);
        return calendar.getTime();
    }

    // 특정 회원의 모든 쿠폰 목록 조회 (만료일자 기준 오름차순)
    @Override
    public List<ReadUserCouponResponse> getAllUserCouponsByUserId(Long userId) {

        // 회원 쿠폰 리스트 가져오기
        List<CouponUser> couponUsers = couponUserRepository.findByUserUserIdAndUserCouponStatus(userId, CouponUser.UserCouponStatus.ACTIVE);

        if (couponUsers.isEmpty()) {
            throw new CouponUserException(ErrorStatus.toErrorStatus("회원 쿠폰이 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        List<Long> couponIds = couponUsers.stream()
                .map(CouponUser::getCouponId)
                .collect(Collectors.toList());

        List<CouponInfoResponse> couponInfoResponses = couponAdaptor.getCouponsInfo(couponIds);

        // CouponInfoResponse를 매핑하기 위한 Map 생성
        Map<Long, CouponInfoResponse> couponInfoMap = couponInfoResponses.stream()
                .collect(Collectors.toMap(CouponInfoResponse::couponId, Function.identity()));

        // ReadUserCouponResponse로 변환 및 만료일자 기준으로 정렬
        return couponUsers.stream()
                .sorted(Comparator.comparing(CouponUser::getCouponExpiredAt))
                .map(couponUser -> {
                    CouponInfoResponse couponInfoResponse = couponInfoMap.get(couponUser.getCouponId());
                    return ReadUserCouponResponse.builder()
                            .userCouponId(couponUser.getUserCouponId())
                            .CouponExpiredAt(couponUser.getCouponExpiredAt())
                            .couponId(couponUser.getCouponId())
                            .couponName(couponInfoResponse.couponName())
                            .couponMinAmount(couponInfoResponse.couponMinAmount())
                            .couponDiscountAmount(couponInfoResponse.couponDiscountAmount())
                            .couponDiscountRate(couponInfoResponse.couponDiscountRate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 회원의 가장 할인 금액이 높은 쿠폰 반환
    @Override
    public ReadMaximumDiscountCouponResponse getMaximumDiscountCouponByUserId(Long userId, ReadMaximumDiscountCouponRequest couponRequest) {

        // 회원 쿠폰 리스트 가져오기
        List<CouponUser> couponUsers = couponUserRepository.findByUserUserIdAndUserCouponStatus(userId, CouponUser.UserCouponStatus.ACTIVE);

        if (couponUsers.isEmpty()) {
            throw new CouponUserException(ErrorStatus.toErrorStatus("회원 쿠폰이 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        List<Long> couponIds = couponUsers.stream()
                .map(CouponUser::getCouponId)
                .collect(Collectors.toList());

        List<CouponInfoResponse> couponInfoResponses = couponAdaptor.getCouponsInfo(couponIds);

        // CouponInfoResponse를 매핑하기 위한 Map 생성
        Map<Long, CouponInfoResponse> couponInfoMap = couponInfoResponses.stream()
                .collect(Collectors.toMap(CouponInfoResponse::couponId, Function.identity()));

        // CouponBoxResponse로 변환 및 만료일자 기준으로 정렬
        List<CouponBoxResponse> couponBoxResponses = couponUsers.stream()
                .sorted(Comparator.comparing(CouponUser::getCouponExpiredAt))
                .map(couponUser -> {
                    CouponInfoResponse couponInfoResponse = couponInfoMap.get(couponUser.getCouponId());
                    return CouponBoxResponse.builder()
                            .couponId(couponUser.getCouponId())
                            .CouponExpiredAt(couponUser.getCouponExpiredAt())
                            .couponMinAmount(couponInfoResponse.couponMinAmount())
                            .couponMaxAmount(couponInfoResponse.couponMaxAmount())
                            .couponDiscountAmount(couponInfoResponse.couponDiscountAmount())
                            .couponDiscountRate(couponInfoResponse.couponDiscountRate())
                            .couponDiscountType(couponInfoResponse.couponDiscountType())
                            .build();
                })
                .toList();

        // 주문 금액
        BigDecimal totalAmount = BigDecimal.valueOf(couponRequest.totalAmount());

        // 최대 할인 쿠폰을 저장할 변수 초기화
        CouponBoxResponse maxDiscountCoupon = null;
        BigDecimal maxDiscountAmount = BigDecimal.ZERO;

        // 각 쿠폰의 할인 금액을 계산하고 최대 할인 쿠폰 선택
        for (CouponBoxResponse userCoupon : couponBoxResponses) {
            BigDecimal discountAmount = calculateDiscountAmount(userCoupon, totalAmount);

            if (discountAmount.compareTo(maxDiscountAmount) > 0) {
                maxDiscountAmount = discountAmount;
                maxDiscountCoupon = userCoupon;
            }
            else if (Objects.nonNull(maxDiscountCoupon) && discountAmount.compareTo(maxDiscountAmount) == 0) {
                // 할인 금액이 동일한 경우 만료일이 더 가까운 쿠폰을 우선 선택
                if (userCoupon.CouponExpiredAt().before(Objects.requireNonNull(maxDiscountCoupon).CouponExpiredAt())) {
                    maxDiscountCoupon = userCoupon;
                }
            }
        }

        // 최대 할인 쿠폰이 없는 경우 (null 처리)
        if (maxDiscountCoupon == null) {

            return ReadMaximumDiscountCouponResponse.builder()
                    .couponId(null)
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        // 최대 할인 쿠폰 반환
        return ReadMaximumDiscountCouponResponse.builder()
                .couponId(maxDiscountCoupon.couponId())
                .discountAmount(maxDiscountAmount)
                .build();
    }

    private BigDecimal calculateDiscountAmount(CouponBoxResponse userCoupon, BigDecimal totalAmount) {
        BigDecimal discountAmount;

        if (!userCoupon.couponDiscountType()) {
            // 정액 할인
            discountAmount = userCoupon.couponDiscountAmount();
        }
        else {
            // 정율 할인
            discountAmount = totalAmount.multiply(userCoupon.couponDiscountRate());
        }

        // 최대 할인 금액 제한 적용
        if (userCoupon.couponMaxAmount() != null && discountAmount.compareTo(userCoupon.couponMaxAmount()) > 0) {
            discountAmount = userCoupon.couponMaxAmount();
        }

        // 최소 주문 금액 조건 체크
        if (userCoupon.couponMinAmount() != null && totalAmount.compareTo(userCoupon.couponMinAmount()) < 0) {
            return BigDecimal.ZERO;
        }

        return discountAmount;
    }

}
