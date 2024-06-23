package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserTotalAmountService;
import com.yes255.yes255booksusersserver.common.exception.UserTotalAmountNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.UserTotalAmount;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserTotalAmountRepository;
import com.yes255.yes255booksusersserver.presentation.dto.response.UserTotalAmountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class UserTotalAmountServiceImpl implements UserTotalAmountService {

    private final JpaUserTotalAmountRepository userTotalAmountRepository;

    @Transactional(readOnly = true)
    @Override
    public UserTotalAmountResponse findUserTotalAmountByUserId(Long userId) {

        UserTotalAmount userTotalAmount = userTotalAmountRepository.findByUser_UserId(userId);

        if (Objects.isNull(userTotalAmount)) {
            throw new UserTotalAmountNotFoundException(ErrorStatus.toErrorStatus("유저 누적 금액이 존재 하지 않습니다.", 400, LocalDateTime.now()));
        }

        return UserTotalAmountResponse.builder()
                .userTotalAmountId(userTotalAmount.getUserTotalAmountId())
                .userTotalAmount(userTotalAmount.getUserTotalAmount())
                .userId(userId)
                .build();
    }

    @Override
    public void deleteUserTotalAmountByUserId(Long userId) {

        UserTotalAmount userTotalAmount = userTotalAmountRepository.findByUser_UserId(userId);

        if (Objects.isNull(userTotalAmount)) {
            throw new UserTotalAmountNotFoundException(ErrorStatus.toErrorStatus("유저 누적 금액이 존재 하지 않습니다.", 400, LocalDateTime.now()));
        }

        userTotalAmountRepository.delete(userTotalAmount);
    }
}
