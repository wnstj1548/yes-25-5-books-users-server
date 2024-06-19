package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.PointService;
import com.yes255.yes255booksusersserver.persistance.domain.Point;
import com.yes255.yes255booksusersserver.persistance.domain.PointPolicy;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserGrade;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointPolicyRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdatePointRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.PointLogResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdatePointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final JpaUserRepository userRepository;
    private final JpaPointRepository pointRepository;
    private final JpaPointPolicyRepository pointPolicyRepository;

    @Override
    public PointLogResponse findAllPointLogByUserId(Long userId) {
        return null;
    }

    @Override
    public UpdatePointResponse updatePointByUserId(Long userId, UpdatePointRequest pointRequest) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("유저 ID가 존재 하지 않습니다."));
//
//        PointPolicy pointPolicy = pointPolicyRepository.findAllById(Collections.singleton(user.getUserState().getUserStateId()))
//                .orElseThrow(() -> new IllegalArgumentException("포인트 정책 ID가 존재 하지 않습니다."));
//
//        Point point = pointRepository.findByUser_UserId(userId);
//
//        BigDecimal tempPoint = point.getPointCurrent()
//                                    .add(pointRequest.amount().multiply(pointPolicy.getPointPolicyRate()))
//                                    .subtract(pointRequest.usePoints());
//
//
//        point.updatePointCurrent(tempPoint);
//
//        pointRepository.save(point);
//
//        return UpdatePointResponse.builder()
//                .point(point.getPointCurrent())
//                .build();

        return null;
    }

    @Override
    public void createPointByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 ID가 존재 하지 않습니다."));

        Point point = Point.builder()
                            .pointCurrent(BigDecimal.valueOf(50000))
                            .user(user)
                            .build();

        pointRepository.save(point);
    }
}
