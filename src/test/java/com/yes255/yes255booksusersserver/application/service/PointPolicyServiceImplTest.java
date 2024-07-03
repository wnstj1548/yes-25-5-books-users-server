package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.PointPolicyServiceImpl;
import com.yes255.yes255booksusersserver.persistance.domain.PointPolicy;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointPolicyRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.CreatePointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.PointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointpolicy.PointPolicyResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointPolicyServiceImplTest {

    @Mock
    private JpaPointPolicyRepository pointPolicyRepository;

    @InjectMocks
    private PointPolicyServiceImpl pointPolicyService;

    @DisplayName("포인트 정책 생성")
    @Test
    void testCreatePointPolicy() {
        CreatePointPolicyRequest request = CreatePointPolicyRequest.builder()
                .pointPolicyName("Test Policy")
                .pointPolicyApply(new BigDecimal("100.00"))
                .pointPolicyCondition("Test condition")
                .pointPolicyApplyType(true)
                .pointPolicyConditionAmount(new BigDecimal("50.00"))
                .build();

        PointPolicy savedPolicy = PointPolicy.builder()
                .pointPolicyId(1L)
                .pointPolicyName(request.pointPolicyName())
                .pointPolicyConditionAmount(request.pointPolicyConditionAmount())
                .pointPolicyCondition(request.pointPolicyCondition())
                .pointPolicyApplyAmount(request.pointPolicyApplyType() ? request.pointPolicyApply() : null)
                .pointPolicyCreatedAt(LocalDate.now())
                .pointPolicyApplyType(true)
                .pointPolicyState(true)
                .build();

        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(savedPolicy);

        PointPolicyResponse response = pointPolicyService.createPointPolicy(request);

        assertNotNull(response);
        assertEquals(savedPolicy.getPointPolicyId(), response.pointPolicyId());
        assertEquals(request.pointPolicyName(), response.pointPolicyName());
        assertEquals(request.pointPolicyApply(), response.pointPolicyApply());
        assertEquals(request.pointPolicyCondition(), response.pointPolicyCondition());
        assertEquals(request.pointPolicyApplyType(), response.pointPolicyApplyType());
        assertEquals(request.pointPolicyConditionAmount(), response.pointPolicyConditionAmount());
        assertTrue(response.pointPolicyState());
    }

    @DisplayName("특정 포인트 정책 조회")
    @Test
    void testFindPointPolicyById() {
        Long policyId = 1L;
        PointPolicy pointPolicy = PointPolicy.builder()
                .pointPolicyId(policyId)
                .pointPolicyName("Test Policy")
                .pointPolicyConditionAmount(new BigDecimal("50.00"))
                .pointPolicyCondition("Test condition")
                .pointPolicyApplyAmount(new BigDecimal("100.00"))
                .pointPolicyCreatedAt(LocalDate.now())
                .pointPolicyApplyType(true)
                .pointPolicyState(true)
                .build();

        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.of(pointPolicy));

        PointPolicyResponse response = pointPolicyService.findPointPolicyById(policyId);

        assertNotNull(response);
        assertEquals(pointPolicy.getPointPolicyId(), response.pointPolicyId());
        assertEquals(pointPolicy.getPointPolicyName(), response.pointPolicyName());
        assertEquals(pointPolicy.isPointPolicyApplyType() ? pointPolicy.getPointPolicyApplyAmount() : pointPolicy.getPointPolicyRate(), response.pointPolicyApply());
        assertEquals(pointPolicy.getPointPolicyCondition(), response.pointPolicyCondition());
        assertEquals(pointPolicy.isPointPolicyApplyType(), response.pointPolicyApplyType());
        assertEquals(pointPolicy.getPointPolicyConditionAmount(), response.pointPolicyConditionAmount());
        assertEquals(pointPolicy.getPointPolicyCreatedAt(), response.pointPolicyCreatedAt());
        assertEquals(pointPolicy.getPointPolicyUpdatedAt() != null ? pointPolicy.getPointPolicyUpdatedAt().toString() : null, response.pointPolicyUpdatedAt());
        assertTrue(response.pointPolicyState());
    }

    @DisplayName("모든 포인트 정책 조회")
    @Test
    void testFindAllPointPolicies() {
        PointPolicy policy1 = PointPolicy.builder()
                .pointPolicyId(1L)
                .pointPolicyName("Policy 1")
                .pointPolicyConditionAmount(new BigDecimal("50.00"))
                .pointPolicyCondition("Condition 1")
                .pointPolicyApplyAmount(new BigDecimal("100.00"))
                .pointPolicyCreatedAt(LocalDate.now())
                .pointPolicyApplyType(true)
                .pointPolicyState(true)
                .build();

        PointPolicy policy2 = PointPolicy.builder()
                .pointPolicyId(2L)
                .pointPolicyName("Policy 2")
                .pointPolicyRate(new BigDecimal("0.1"))
                .pointPolicyCondition("Condition 2")
                .pointPolicyCreatedAt(LocalDate.now())
                .pointPolicyApplyType(false)
                .pointPolicyState(true)
                .build();

        List<PointPolicy> policies = Arrays.asList(policy1, policy2);
        Page<PointPolicy> page = new PageImpl<>(policies);

        when(pointPolicyRepository.findAllByOrderByPointPolicyCreatedAtAscPointPolicyStateDesc(Pageable.unpaged())).thenReturn(page);

        List<PointPolicyResponse> responses = pointPolicyService.findAllPointPolicies(Pageable.unpaged()).getContent();

        assertNotNull(responses);
        assertEquals(policies.size(), responses.size());

        for (int i = 0; i < policies.size(); i++) {
            assertEquals(policies.get(i).getPointPolicyId(), responses.get(i).pointPolicyId());
            assertEquals(policies.get(i).getPointPolicyName(), responses.get(i).pointPolicyName());
            assertEquals(policies.get(i).isPointPolicyApplyType() ? policies.get(i).getPointPolicyApplyAmount() : policies.get(i).getPointPolicyRate(), responses.get(i).pointPolicyApply());
            assertEquals(policies.get(i).getPointPolicyCondition(), responses.get(i).pointPolicyCondition());
            assertEquals(policies.get(i).isPointPolicyApplyType(), responses.get(i).pointPolicyApplyType());
            assertEquals(policies.get(i).getPointPolicyConditionAmount(), responses.get(i).pointPolicyConditionAmount());
            assertEquals(policies.get(i).getPointPolicyCreatedAt(), responses.get(i).pointPolicyCreatedAt());
            assertEquals(policies.get(i).getPointPolicyUpdatedAt() != null ? policies.get(i).getPointPolicyUpdatedAt().toString() : null, responses.get(i).pointPolicyUpdatedAt());
            assertTrue(responses.get(i).pointPolicyState());
        }
    }

    @DisplayName("특정 포인트 정책 업데이트")
    @Test
    void testUpdatePointPolicyById() {
        Long policyId = 1L;
        PointPolicyRequest requestTrue = PointPolicyRequest.builder()
                .pointPolicyName("Updated Policy True")
                .pointPolicyApply(new BigDecimal("200.00"))
                .pointPolicyCondition("Updated condition")
                .pointPolicyApplyType(true)
                .pointPolicyConditionAmount(new BigDecimal("80.00"))
                .build();

        PointPolicyRequest requestFalse = PointPolicyRequest.builder()
                .pointPolicyName("Updated Policy False")
                .pointPolicyApply(new BigDecimal("0.15"))
                .pointPolicyCondition("Updated condition")
                .pointPolicyApplyType(false)
                .pointPolicyConditionAmount(new BigDecimal("80.00"))
                .build();

        PointPolicy existingPolicy = PointPolicy.builder()
                .pointPolicyId(policyId)
                .pointPolicyName("Old Policy")
                .pointPolicyConditionAmount(new BigDecimal("50.00"))
                .pointPolicyCondition("Old condition")
                .pointPolicyApplyAmount(new BigDecimal("100.00"))
                .pointPolicyCreatedAt(LocalDate.now())
                .pointPolicyApplyType(true)
                .pointPolicyState(true)
                .build();

        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.of(existingPolicy));
        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(existingPolicy);

        // pointPolicyApplyType : true
        assertDoesNotThrow(() -> {
            PointPolicyResponse response = pointPolicyService.updatePointPolicyById(policyId, requestTrue);

            assertNotNull(response);
            assertEquals(policyId, response.pointPolicyId());
            assertEquals(requestTrue.pointPolicyName(), response.pointPolicyName());
            assertEquals(requestTrue.pointPolicyApply(), response.pointPolicyApply());
            assertEquals(requestTrue.pointPolicyCondition(), response.pointPolicyCondition());
            assertEquals(requestTrue.pointPolicyApplyType(), response.pointPolicyApplyType());
            assertEquals(requestTrue.pointPolicyConditionAmount(), response.pointPolicyConditionAmount());
        });

        // pointPolicyApplyType : false
        assertDoesNotThrow(() -> {
            PointPolicyResponse response = pointPolicyService.updatePointPolicyById(policyId, requestFalse);

            assertNotNull(response);
            assertEquals(policyId, response.pointPolicyId());
            assertEquals(requestFalse.pointPolicyName(), response.pointPolicyName());
            assertEquals(requestFalse.pointPolicyApply(), response.pointPolicyApply());
            assertEquals(requestFalse.pointPolicyCondition(), response.pointPolicyCondition());
            assertEquals(requestFalse.pointPolicyApplyType(), response.pointPolicyApplyType());
            assertEquals(requestFalse.pointPolicyConditionAmount(), response.pointPolicyConditionAmount());
        });
    }

    @DisplayName("특정 포인트 정책 삭제")
    @Test
    void testDeletePointPolicyById() {
        Long policyId = 1L;
        PointPolicy existingPolicy = PointPolicy.builder()
                .pointPolicyId(policyId)
                .pointPolicyName("Policy")
                .pointPolicyState(true)
                .build();

        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.of(existingPolicy));
        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(existingPolicy);

        assertDoesNotThrow(() -> pointPolicyService.deletePointPolicyById(policyId));

        verify(pointPolicyRepository, times(1)).findById(policyId);
        verify(pointPolicyRepository, times(1)).save(existingPolicy);
        assertFalse(existingPolicy.isPointPolicyState());
    }
}
