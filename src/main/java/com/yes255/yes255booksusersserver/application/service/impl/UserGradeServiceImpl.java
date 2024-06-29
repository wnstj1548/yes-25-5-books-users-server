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
    public void updateUserGrade(User user, BigDecimal purePrices, LocalDate currentDate) {

        UserGradeLog lastUserGradeLog = userGradeLogRepository.findFirstByUserUserIdOrderByUserGradeUpdatedAtDesc(user.getUserId())
                .orElseThrow(() -> new UserGradeLogException(ErrorStatus.toErrorStatus("회원 등급 변경 이력이 존재하지 않습니다.", 400, LocalDateTime.now())));

        UserGrade setUserGrade = null;

        List<UserGrade> userGrades = userGradeRepository.findAll();
        userGrades.sort((grade1, grade2) -> grade2.getPointPolicy().getPointPolicyConditionAmount().compareTo(grade1.getPointPolicy().getPointPolicyConditionAmount()));

        if (userGrades.isEmpty()) {
            throw new UserGradeException(ErrorStatus.toErrorStatus("회원 등급이 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        for (UserGrade userGrade : userGrades) {
            if (!userGrade.getPointPolicy().isPointPolicyApplyType()
                    && purePrices.compareTo(userGrade.getPointPolicy().getPointPolicyConditionAmount()) >= 0) {
                setUserGrade = userGrade;
                break;
            }
        }

        if (Objects.isNull(setUserGrade)) {
            throw new UserGradeException(ErrorStatus.toErrorStatus("회원 등급이 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        // 3개월이 지났는지 확인
        boolean isThreeMonthsPassed = lastUserGradeLog.getUserGradeUpdatedAt().plusMonths(3).isBefore(currentDate);

        // 등급이 상향되었는지 확인
        boolean isUpgrade = user.getUserGrade().getPointPolicy().getPointPolicyConditionAmount().compareTo(setUserGrade.getPointPolicy().getPointPolicyConditionAmount()) < 0;

        if (isUpgrade || isThreeMonthsPassed) {
            user.updateUserGrade(setUserGrade);
            userRepository.save(user);

            userGradeLogRepository.save(UserGradeLog.builder()
                    .userGrade(setUserGrade)
                    .userGradeUpdatedAt(LocalDate.now())
                    .user(user)
                    .build());
        }
    }

    // 매달 1일 마다 확인
    @Scheduled(cron = "0 0 0 1 * ?")
    public void processMonthlyGrades() {
        LocalDate currentDate = LocalDate.now();

        // 주문 서버로부터 3개월 치 순수 금액 내역 반환
        List<OrderLogResponse> orderLogResponses = orderAdaptor.getOrderLogs();

        for (OrderLogResponse orderLogResponse : orderLogResponses) {

            User user = userRepository.findById(orderLogResponse.customerId())
                    .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

            updateUserGrade(user, orderLogResponse.purePrices(), currentDate);
        }
    }
}
