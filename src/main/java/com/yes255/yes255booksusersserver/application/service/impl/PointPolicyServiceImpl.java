package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.PointPolicyService;
import com.yes255.yes255booksusersserver.common.exception.PointPolicyException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.PointPolicy;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointPolicyRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.CreatePointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.PointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointpolicy.PointPolicyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final JpaPointPolicyRepository pointPolicyRepository;

    // 포인트 정책 생성
    @Override
    public PointPolicyResponse createPointPolicy(CreatePointPolicyRequest policyRequest) {

        PointPolicy pointPolicy = pointPolicyRepository.save(policyRequest.toEntity());

        return PointPolicyResponse.builder()
                .pointPolicyId(pointPolicy.getPointPolicyId())
                .pointPolicyName(policyRequest.pointPolicyName())
                .pointPolicyApply(policyRequest.pointPolicyApply())
                .pointPolicyCondition(policyRequest.pointPolicyCondition())
                .pointPolicyApplyType(policyRequest.pointPolicyApplyType())
                .pointPolicyConditionAmount(policyRequest.pointPolicyConditionAmount())
                .pointPolicyState(true)
                .build();
    }

    // 포인트 정책 단건 조회
    @Transactional(readOnly = true)
    @Override
    public PointPolicyResponse findPointPolicyById(Long pointPolicyId) {

        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new PointPolicyException(ErrorStatus.toErrorStatus("포인트 정책을 찾을 수 없습니다.", 400, LocalDateTime.now())));

        return PointPolicyResponse.builder()
                .pointPolicyId(pointPolicy.getPointPolicyId())
                .pointPolicyName(pointPolicy.getPointPolicyName())
                .pointPolicyApply(pointPolicy.isPointPolicyApplyType() ? pointPolicy.getPointPolicyApplyAmount() : pointPolicy.getPointPolicyRate())
                .pointPolicyCondition(pointPolicy.getPointPolicyCondition())
                .pointPolicyConditionAmount(pointPolicy.getPointPolicyConditionAmount())
                .pointPolicyApplyType(pointPolicy.isPointPolicyApplyType())
                .pointPolicyCreatedAt(pointPolicy.getPointPolicyCreatedAt())
                .pointPolicyUpdatedAt(pointPolicy.getPointPolicyUpdatedAt() != null ? pointPolicy.getPointPolicyUpdatedAt().toString() : null)
                .pointPolicyState(pointPolicy.isPointPolicyState())
                .build();
    }

    // 포인트 정책 목록 조회
    @Transactional(readOnly = true)
    @Override
    public Page<PointPolicyResponse> findAllPointPolicies(Pageable pageable) {

        Page<PointPolicy> pointPolicies = pointPolicyRepository.findAllByOrderByPointPolicyCreatedAtAscPointPolicyStateDesc(pageable);

        return pointPolicies.map(pointPolicy -> PointPolicyResponse.builder()
                .pointPolicyId(pointPolicy.getPointPolicyId())
                .pointPolicyName(pointPolicy.getPointPolicyName())
                .pointPolicyApply(pointPolicy.isPointPolicyApplyType() ?
                        pointPolicy.getPointPolicyApplyAmount() : pointPolicy.getPointPolicyRate())
                .pointPolicyCondition(pointPolicy.getPointPolicyCondition())
                .pointPolicyConditionAmount(pointPolicy.getPointPolicyConditionAmount())
                .pointPolicyApplyType(pointPolicy.isPointPolicyApplyType())
                .pointPolicyCreatedAt(pointPolicy.getPointPolicyCreatedAt())
                .pointPolicyUpdatedAt(pointPolicy.getPointPolicyUpdatedAt() != null ?
                        pointPolicy.getPointPolicyUpdatedAt().toString() : null)
                .pointPolicyState(pointPolicy.isPointPolicyState())
                .build());
    }

    @Override
    public PointPolicyResponse updatePointPolicyById(Long pointPolicyId, PointPolicyRequest policyRequest) {

        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new PointPolicyException(ErrorStatus.toErrorStatus("포인트 정책을 찾을 수 없습니다.", 400, LocalDateTime.now())));

        pointPolicy.updatePointPolicyName(policyRequest.pointPolicyName());

        if (policyRequest.pointPolicyApplyType()) {
            pointPolicy.updatePointPolicyApplyAmount(policyRequest.pointPolicyApply());
            pointPolicy.updatePointPolicyConditionAmount(null);
            pointPolicy.updatePointPolicyRate(null);
        }
        else {
            pointPolicy.updatePointPolicyRate(policyRequest.pointPolicyApply());
            pointPolicy.updatePointPolicyConditionAmount(policyRequest.pointPolicyConditionAmount());
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
                .pointPolicyConditionAmount(policyRequest.pointPolicyConditionAmount())
                .build();
    }

    @Override
    public void deletePointPolicyById(Long pointPolicyId) {

        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new PointPolicyException(ErrorStatus.toErrorStatus("포인트 정책을 찾을 수 없습니다.", 400, LocalDateTime.now())));

        pointPolicy.updatePointPolicyState(false);
        pointPolicy.updatePointPolicyUpdatedAt();

        pointPolicyRepository.save(pointPolicy);
    }
}
