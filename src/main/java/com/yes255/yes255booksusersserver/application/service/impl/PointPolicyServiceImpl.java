package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.PointPolicyService;
import com.yes255.yes255booksusersserver.persistance.domain.PointPolicy;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointPolicyRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.PointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.PointPolicyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final JpaPointPolicyRepository pointPolicyRepository;

    // 포인트 정책 생성
    @Override
    public PointPolicyResponse createPointPolicy(PointPolicyRequest policyRequest) {

        PointPolicy pointPolicy = pointPolicyRepository.save(policyRequest.toEntity());

        return PointPolicyResponse.builder()
                .pointPolicyId(pointPolicy.getPointPolicyId())
                .pointPolicyName(policyRequest.pointPolicyName())
                .pointPolicyApply(policyRequest.pointPolicyApply())
                .pointPolicyCondition(policyRequest.pointPolicyCondition())
                .pointPolicyApplyType(policyRequest.pointPolicyApplyType())
                .build();
    }

    // 포인트 정책 단건 조회
    @Override
    public PointPolicyResponse findPointPolicyById(Long pointPolicyId) {

        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정책을 찾을 수 없습니다."));

        return PointPolicyResponse.builder()
                .pointPolicyId(pointPolicy.getPointPolicyId())
                .pointPolicyName(pointPolicy.getPointPolicyName())
                .pointPolicyApply(pointPolicy.isPointPolicyApplyType() ? pointPolicy.getPointPolicyApplyAmount() : pointPolicy.getPointPolicyRate())
                .pointPolicyCondition(pointPolicy.getPointPolicyCondition())
                .pointPolicyApplyType(pointPolicy.isPointPolicyApplyType())
                .pointPolicyCreatedAt(pointPolicy.getPointPolicyCreatedAt())
                .pointPolicyUpdatedAt(pointPolicy.getPointPolicyUpdatedAt() != null ? pointPolicy.getPointPolicyUpdatedAt().toString() : null)
                .build();
    }

    // 포인트 정책 목록 조회
    @Override
    public List<PointPolicyResponse> findAllPointPolicies() {

        List<PointPolicy> pointPolicies = pointPolicyRepository.findAll();

        return pointPolicies.stream()
                .map(pointPolicy -> PointPolicyResponse.builder()
                        .pointPolicyId(pointPolicy.getPointPolicyId())
                        .pointPolicyName(pointPolicy.getPointPolicyName())
                        .pointPolicyApply(pointPolicy.isPointPolicyApplyType() ? pointPolicy.getPointPolicyApplyAmount() : pointPolicy.getPointPolicyRate())
                        .pointPolicyCondition(pointPolicy.getPointPolicyCondition())
                        .pointPolicyApplyType(pointPolicy.isPointPolicyApplyType())
                        .pointPolicyCreatedAt(pointPolicy.getPointPolicyCreatedAt())
                        .pointPolicyUpdatedAt(pointPolicy.getPointPolicyUpdatedAt() != null ? pointPolicy.getPointPolicyUpdatedAt().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public PointPolicyResponse updatePointPolicyById(Long pointPolicyId, PointPolicyRequest policyRequest) {

        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정책을 찾을 수 없습니다."));

        pointPolicy.updatePointPolicyName(policyRequest.pointPolicyName());

        if (policyRequest.pointPolicyApplyType()) {
            pointPolicy.updatePointPolicyApplyAmount(policyRequest.pointPolicyApply());
            pointPolicy.updatePointPolicyRate(null);
        }
        else {
            pointPolicy.updatePointPolicyRate(policyRequest.pointPolicyApply());
            pointPolicy.updatePointPolicyApplyAmount(null);
        }

        pointPolicy.updatePointPolicyCondition(policyRequest.pointPolicyCondition());
        pointPolicy.updatePointPolicyApplyType(policyRequest.pointPolicyApplyType());
        pointPolicy.updatePointPolicyUpdatedAt();

        pointPolicyRepository.save(pointPolicy);

        return PointPolicyResponse.builder()
                .pointPolicyId(pointPolicy.getPointPolicyId())
                .pointPolicyName(policyRequest.pointPolicyName())
                .pointPolicyApply(policyRequest.pointPolicyApply())
                .pointPolicyCondition(policyRequest.pointPolicyCondition())
                .pointPolicyApplyType(policyRequest.pointPolicyApplyType())
                .build();
    }

    @Override
    public void deletePointPolicyById(Long pointPolicyId) {
        pointPolicyRepository.deleteById(pointPolicyId);
    }
}
