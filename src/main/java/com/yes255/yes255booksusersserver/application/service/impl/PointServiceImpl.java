package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.PointService;
import com.yes255.yes255booksusersserver.common.exception.InsufficientPointsException;
import com.yes255.yes255booksusersserver.common.exception.PointNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.UserNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Point;
import com.yes255.yes255booksusersserver.persistance.domain.PointLog;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserTotalAmount;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointLogRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointPolicyRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserTotalAmountRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.point.UpdatePointRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.point.PointResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.point.UpdatePointResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final JpaUserRepository userRepository;
    private final JpaUserGradeRepository userGradeRepository;
    private final JpaPointRepository pointRepository;
    private final JpaPointPolicyRepository pointPolicyRepository;
    private final JpaPointLogRepository pointLogRepository;
    private final JpaUserTotalAmountRepository totalAmountRepository;

    // 포인트 조회
    @Transactional(readOnly = true)
    public PointResponse findPointByUserId(Long userId) {

        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new PointNotFoundException(ErrorStatus.toErrorStatus("포인트가 존재하지 않습니다.", 400, LocalDateTime.now())));

        return PointResponse.builder()
                .point(point.getPointCurrent())
                .build();
    }


    // 포인트 사용 및 적립
    @Override
    public UpdatePointResponse updatePointByUserId(Long userId, UpdatePointRequest pointRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

        // 입력 값 검증 및 치환
        BigDecimal usePoints = pointRequest.usePoints() != null && pointRequest.usePoints().compareTo(BigDecimal.ZERO) > 0
                ? pointRequest.usePoints()
                : BigDecimal.ZERO;

        BigDecimal amount = pointRequest.amount() != null && pointRequest.amount().compareTo(BigDecimal.ZERO) > 0
                ? pointRequest.amount()
                : BigDecimal.ZERO;

        Point point = pointRepository.findByUser_UserId(userId);

        BigDecimal tempPoint = point.getPointCurrent()
                .add(amount.multiply(user.getUserGrade().getPointPolicy().getPointPolicyRate()))
                .subtract(usePoints);

        if (tempPoint.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientPointsException(ErrorStatus.toErrorStatus("포인트가 부족합니다.", 400, LocalDateTime.now()));
        }

        point.updatePointCurrent(tempPoint);
        pointRepository.save(point);

        // 구매 누적 금액 갱신
        UserTotalAmount userTotalAmount = totalAmountRepository.findByUser_UserId(userId);
        userTotalAmount.updateTotalAmount(pointRequest.amount());

        totalAmountRepository.save(userTotalAmount);

        // 포인트로 구매 시 포인트 이력 추가
        if (usePoints.compareTo(BigDecimal.ZERO) > 0) {

            pointLogRepository.save(PointLog.builder()
                    .pointLogUpdatedAt(LocalDateTime.now())
                    .pointLogUpdatedType("사용")
                    .pointLogAmount(usePoints)
                    .point(point)
                    .build());
        }

        // 구매 시 구매 금액에 따른 포인트 적립 이력 추가
        if (amount.compareTo(BigDecimal.ZERO) > 0) {

            pointLogRepository.save(PointLog.builder()
                            .pointLogUpdatedAt(LocalDateTime.now())
                            .pointLogUpdatedType("적립")
                            .pointLogAmount(amount.multiply(user.getUserGrade().getPointPolicy().getPointPolicyRate()))
                            .point(point)
                            .build());
        }

        // 회원 등급 갱신 체크 및 적용
//        List<UserGrade> userGrades = userGradeRepository.findAll();
//        for (UserGrade userGrade : userGrades) {
//
//            PointPolicy policy = userGrade.getPointPolicy();
//
//            if (Objects.nonNull(userTotalAmount.getUserTotalAmount()) && policy.getPointPolicyConditionAmount().compareTo(BigDecimal.ZERO) != 0 &&
//                    !policy.isPointPolicyApplyType() &&
//                    userTotalAmount.getUserTotalAmount().compareTo(policy.getPointPolicyConditionAmount()) >= 0) {
//
//                user.updateUserGrade(userGrade);
//            }
//        }
        // todo : 갱신 로직 스케줄러로 구현

        return UpdatePointResponse.builder()
                .point(point.getPointCurrent())
                .build();
    }
}
