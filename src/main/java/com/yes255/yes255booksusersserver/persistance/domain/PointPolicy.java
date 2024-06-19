package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointPolicyId;

    @NotNull(message = "포인트 정책명은 필수입니다.")
    @Column(nullable = false, length = 50)
    private String pointPolicyName;

    // 적립률
    private BigDecimal pointPolicyRate;

    // 적립 금액
    @Column(precision = 10, scale = 2)
    private BigDecimal pointPolicyApplyAmount;

    // 적립 조건
    @NotNull(message = "포인트 적립 조건은 필수입니다.")
    @Column(nullable = false, length = 200)
    private String pointPolicyCondition;

    // 적립 유형
    // 적립 금액 = 1(true), 적립률 = 0(false)
    @NotNull(message = "포인트 적립 유형은 필수입니다.")
    @Column(nullable = false)
    private boolean pointPolicyApplyType;

    @NotNull(message = "포인트 생성일은 필수입니다.")
    @Column(nullable = false)
    private LocalDate pointPolicyCreatedAt;

    private LocalDate pointPolicyUpdatedAt;

    @Builder
    public PointPolicy(Long pointPolicyId, String pointPolicyName, BigDecimal pointPolicyRate, String pointPolicyCondition,
                       BigDecimal pointPolicyApplyAmount, LocalDate pointPolicyCreatedAt,
                       LocalDate pointPolicyUpdatedAt, boolean pointPolicyApplyType) {

        this.pointPolicyId = pointPolicyId;
        this.pointPolicyName = pointPolicyName;
        this.pointPolicyRate = pointPolicyRate;
        this.pointPolicyCondition = pointPolicyCondition;
        this.pointPolicyApplyAmount = pointPolicyApplyAmount;
        this.pointPolicyApplyType = pointPolicyApplyType;
        this.pointPolicyCreatedAt = pointPolicyCreatedAt;
        this.pointPolicyUpdatedAt = pointPolicyUpdatedAt;
    }

    public void updatePointPolicyName(String pointPolicyName) {
        this.pointPolicyName = pointPolicyName;
    }

    public void updatePointPolicyApplyAmount(BigDecimal pointPolicyApplyAmount) {
        this.pointPolicyApplyAmount = pointPolicyApplyAmount;
    }

    public void updatePointPolicyRate(BigDecimal pointPolicyRate) {
        this.pointPolicyRate = pointPolicyRate;
    }

    public void updatePointPolicyCondition(String pointPolicyCondition) {
        this.pointPolicyCondition = pointPolicyCondition;
    }

    public void updatePointPolicyUpdatedAt() {
        this.pointPolicyUpdatedAt = LocalDate.now();
    }
}
