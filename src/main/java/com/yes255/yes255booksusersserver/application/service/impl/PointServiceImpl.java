package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.PointService;
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

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final JpaUserRepository userRepository;
    private final JpaUserGradeRepository userGradeRepository;
    private final JpaPointRepository pointRepository;
    private final JpaPointPolicyRepository pointPolicyRepository;

    // 포인트 조회
    @Override
    public PointResponse findPointByUserId(Long userId) {

        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));

        return PointResponse.builder()
                .point(point.getPointCurrent())
                .build();
    }

    @Override
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
                                    .add(pointRequest.amount().multiply(pointPolicy.getPointPolicyRate()))
                                    .subtract(pointRequest.usePoints());

        if (tempPoint.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }


        point.updatePointCurrent(tempPoint);

        pointRepository.save(point);

        // todo : 포인트 이력 추가

        return UpdatePointResponse.builder()
                .point(point.getPointCurrent())
                .build();
    }
}
