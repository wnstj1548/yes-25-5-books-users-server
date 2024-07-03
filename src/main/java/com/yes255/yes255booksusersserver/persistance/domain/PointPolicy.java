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

    // 조건 금액 (ex : if 00금액, 00금액 이상으로 적용)
    @Column(precision = 10, scale = 2)
    private BigDecimal pointPolicyConditionAmount;

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

    @NotNull(message = "포인트 상태는 필수입니다.")
    @Column(nullable = false)
    private boolean pointPolicyState;

    @Builder
    public PointPolicy(Long pointPolicyId, String pointPolicyName, BigDecimal pointPolicyRate, BigDecimal pointPolicyConditionAmount,
                       String pointPolicyCondition, BigDecimal pointPolicyApplyAmount, LocalDate pointPolicyCreatedAt,
                       LocalDate pointPolicyUpdatedAt, boolean pointPolicyApplyType, boolean pointPolicyState) {

        this.pointPolicyId = pointPolicyId;
        this.pointPolicyName = pointPolicyName;
        this.pointPolicyConditionAmount = pointPolicyConditionAmount;
        this.pointPolicyRate = pointPolicyRate;
        this.pointPolicyCondition = pointPolicyCondition;
        this.pointPolicyApplyAmount = pointPolicyApplyAmount;
        this.pointPolicyApplyType = pointPolicyApplyType;
        this.pointPolicyCreatedAt = pointPolicyCreatedAt;
        this.pointPolicyUpdatedAt = pointPolicyUpdatedAt;
        this.pointPolicyState = pointPolicyState;
    }

    public void updatePointPolicyName(String pointPolicyName) {
        this.pointPolicyName = pointPolicyName;
    }


    public void updatePointPolicyConditionAmount(BigDecimal pointPolicyConditionAmount) {
        this.pointPolicyConditionAmount = pointPolicyConditionAmount;
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


    public void updatePointPolicyApplyType(boolean pointPolicyApplyType) {
        this.pointPolicyApplyType = pointPolicyApplyType;
    }

    public void updatePointPolicyUpdatedAt() {
        this.pointPolicyUpdatedAt = LocalDate.now();
    }

    public void updatePointPolicyState(boolean pointPolicyState) {
        this.pointPolicyState = pointPolicyState;
    }
}
