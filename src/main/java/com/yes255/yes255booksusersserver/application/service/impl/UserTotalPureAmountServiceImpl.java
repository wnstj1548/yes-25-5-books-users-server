package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserTotalPureAmountService;
import com.yes255.yes255booksusersserver.common.exception.UserTotalPureAmountException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.UserTotalPureAmount;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserTotalPureAmountRepository;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadPurePriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class UserTotalPureAmountServiceImpl implements UserTotalPureAmountService {

    private final JpaUserTotalPureAmountRepository userTotalAmountRepository;

    // 주문 서버에 회원의 3개월치 순수 금액 반환
    @Transactional(readOnly = true)
    @Override
    public ReadPurePriceResponse findUserTotalPureAmountByUserId(Long userId) {

        UserTotalPureAmount userTotalPureAmount = userTotalAmountRepository.findFirstByUserUserIdOrderByUserTotalPureAmountRecordedAtDesc(userId)
                .orElseThrow(() -> new UserTotalPureAmountException(ErrorStatus.toErrorStatus("회원 순수 누적 금액이 존재 하지 않습니다.", 400, LocalDateTime.now())));

        return ReadPurePriceResponse.builder()
                .purePrice(userTotalPureAmount.getUserTotalPureAmount())
                .recordedAt(userTotalPureAmount.getUserTotalPureAmountRecordedAt())
                .build();
    }

    @Override
    public void deleteUserTotalPureAmountByUserId(Long userId) {

        UserTotalPureAmount userTotalPureAmount = userTotalAmountRepository.findByUserUserId(userId)
                .orElseThrow(() -> new UserTotalPureAmountException(ErrorStatus.toErrorStatus("회원 순수 누적 금액이 존재 하지 않습니다.", 400, LocalDateTime.now())));

        userTotalAmountRepository.delete(userTotalPureAmount);
    }
}
