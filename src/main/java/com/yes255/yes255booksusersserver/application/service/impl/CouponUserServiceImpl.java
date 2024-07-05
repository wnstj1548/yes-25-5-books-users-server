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
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.CouponBoxResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.CouponInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.ExpiredCouponUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

//        if (couponUsers.isEmpty()) {
//            throw new CouponUserException(ErrorStatus.toErrorStatus("사용된 회원 쿠폰이 존재하지 않습니다.", 400, LocalDateTime.now()));
//        }

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

    // 매일 ??에 한 달 지난 만료된 쿠폰 삭제
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
}
