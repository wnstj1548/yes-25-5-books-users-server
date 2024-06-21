package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.PointService;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.persistance.domain.Point;
import com.yes255.yes255booksusersserver.persistance.domain.PointPolicy;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserGrade;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointPolicyRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdatePointRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.PointResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdatePointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
                .orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));

        return PointResponse.builder()
                .point(point.getPointCurrent())
                .build();
    }


    // 포인트 사용 및 적립
    @Override
    public UpdatePointResponse updatePointByUserId(Long userId, UpdatePointRequest pointRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 입력 값 검증 및 치환
        BigDecimal usePoints = pointRequest.usePoints() != null && pointRequest.usePoints().compareTo(BigDecimal.ZERO) > 0
                ? pointRequest.usePoints()
                : BigDecimal.ZERO;

        BigDecimal amount = pointRequest.amount() != null && pointRequest.amount().compareTo(BigDecimal.ZERO) > 0
                ? pointRequest.amount()
                : BigDecimal.ZERO;

        // 회원 등급에 관한 정책 가져오기
        PointPolicy pointPolicy = pointPolicyRepository.findById(user.getUserGrade().getPointPolicy().getPointPolicyId())

    public UpdatePointResponse updatePointByUserId(Long userId, UpdatePointRequest pointRequest) {

        List<String> policyNames = List.of("Normal", "Royal", "Gold", "Platinum");

        // 유저가 가진 회원 등급 중에 정책 이름이 Normal, Royal, Gold, Platinum인 등급만 필터링
        List<UserGrade> filteredUserGrades = userGradeRepository.findByUser_UserIdAndPointPolicy_PointPolicyNameIn(userId, policyNames);

        if (filteredUserGrades.size() != 1) {
            throw new IllegalArgumentException("회원 등급은 2개 이상일 수 없습니다.");
        }

        PointPolicy pointPolicy = pointPolicyRepository.findById(filteredUserGrades.getFirst().getPointPolicy().getPointPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("포인트 정책을 찾을 수 없습니다."));

        Point point = pointRepository.findByUser_UserId(userId);

        BigDecimal tempPoint = point.getPointCurrent()
                .add(amount.multiply(pointPolicy.getPointPolicyRate()))
                .subtract(usePoints);

        if (tempPoint.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        point.updatePointCurrent(tempPoint);
        pointRepository.save(point);

        // 구매 누적 금액 갱신
        UserTotalAmount userTotalAmount = totalAmountRepository.findByUser_UserId(userId);
        userTotalAmount.updateTotalAmount(pointRequest.amount());

        totalAmountRepository.save(userTotalAmount);


        // todo : 갱신 체크 및 적용 추가
        // 회원 등급 갱신 체크 및 적용
        UserTotalAmount totalAmount = totalAmountRepository.findByUser_UserId(userId);
//        if (Objects.nonNull(totalAmount.getUserTotalAmount())&&
//                totalAmount.getUserTotalAmount().compareTo(user.getUserGrade().getPointPolicy().getPointPolicyApplyAmount()) >= 0) {
//            User.
//        }

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
                            .pointLogAmount(amount.multiply(pointPolicy.getPointPolicyRate()))
                            .point(point)
                            .build());
        }

        return UpdatePointResponse.builder()
                .point(point.getPointCurrent())
                .build();
    }
}
