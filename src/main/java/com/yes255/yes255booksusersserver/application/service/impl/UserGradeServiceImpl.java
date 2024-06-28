package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserGradeService;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.common.exception.UserGradeException;
import com.yes255.yes255booksusersserver.common.exception.UserGradeLogException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.OrderAdaptor;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserGrade;
import com.yes255.yes255booksusersserver.persistance.domain.UserGradeLog;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeLogRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.response.OrderLogResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.usergrade.UserGradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class UserGradeServiceImpl implements UserGradeService {

    final JpaUserRepository userRepository;
    final JpaUserGradeRepository userGradeRepository;
    final JpaUserGradeLogRepository userGradeLogRepository;

    final OrderAdaptor orderAdaptor;

    // 회원 등급 조회
    @Override
    public UserGradeResponse getUserGrade(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

        UserGrade userGrade = user.getUserGrade();

        return UserGradeResponse.builder()
                .userGradeId(userGrade.getUserGradeId())
                .userGradeName(userGrade.getUserGradeName())
                .pointPolicyId(userGrade.getPointPolicy().getPointPolicyId())
                .pointPolicyCondition(userGrade.getPointPolicy().getPointPolicyCondition())
                .build();
    }

    // 회원 등급 갱신
    public void updateUserGrade(User user, BigDecimal purePrices) {

        String gradeName = null;

        List<UserGrade> userGrades = userGradeRepository.findAll();

        if (userGrades.isEmpty()) {
            throw new UserGradeException(ErrorStatus.toErrorStatus("회원 등급이 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        for (UserGrade userGrade : userGrades) {
            if (!userGrade.getPointPolicy().isPointPolicyApplyType()
                    && purePrices.compareTo(userGrade.getPointPolicy().getPointPolicyConditionAmount()) >= 0) {
                gradeName = userGrade.getUserGradeName();
            }
        }


//
//        if (purePrices.compareTo(BigDecimal.valueOf(300000)) >= 0) {
//            gradeName = "PLATINUM";
//        } else if (purePrices.compareTo(BigDecimal.valueOf(200000)) >= 0) {
//            gradeName = "GOLD";
//        } else if (purePrices.compareTo(BigDecimal.valueOf(100000)) >= 0) {
//            gradeName = "ROYAL";
//        } else {
//            gradeName = "NORMAL";
//        }

        UserGrade userGrade = userGradeRepository.findByUserGradeName(gradeName);

        if (Objects.isNull(userGrade)) {
            throw new UserGradeException(ErrorStatus.toErrorStatus("회원 등급이 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        if (!user.getUserGrade().getUserGradeName().equals(gradeName)) {
            user.updateUserGrade(userGrade);
            userRepository.save(user);
        }

        // 등급이 유지되어도 변동 이력에 기록
        userGradeLogRepository.save(UserGradeLog.builder()
                .userGrade(userGrade)
                .userGradeUpdatedAt(LocalDate.now())
                .user(user)
                .build());
    }

    // 매달 1일 마다 확인
    @Scheduled(cron = "0 0 0 1 * ?")
    public void processMonthlyGrades() {
        LocalDate currentDate = LocalDate.now();

        List<OrderLogResponse> orderLogResponses = orderAdaptor.getOrderLogs();

        for (OrderLogResponse orderLogResponse : orderLogResponses) {

            User user = userRepository.findById(orderLogResponse.customerId())
                    .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

            updateUserGrade(user, orderLogResponse.purePrices());
            checkGradeExpiration(user, currentDate, orderLogResponse.purePrices());
        }
    }

    // 3개월 이내에 변동 확인
    private void checkGradeExpiration(User user, LocalDate currentDate, BigDecimal purePrices) {

        // 회원의 최근 변경 이력 반환
        UserGradeLog userGradeLog = userGradeLogRepository.findFirstByUserUserIdOrderByUserGradeUpdatedAtDesc(user.getUserId())
                .orElseThrow(() -> new UserGradeLogException(ErrorStatus.toErrorStatus("회원 등급 변경 이력이 존재하지 않습니다.", 400, LocalDateTime.now())));

        if (!user.getUserGrade().getUserGradeName().equals("NORMAL") &&
            userGradeLog.getUserGradeUpdatedAt().plusMonths(3).isBefore(currentDate)) {
            updateUserGrade(user, purePrices);
        }

    }
}