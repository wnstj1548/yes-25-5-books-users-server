package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
public class PointPolicy {
    @Id
    private Long pointPolicyId;

    private Long pointPolicyName;

    private BigDecimal pointPolicyRate;

    private String pointPolicyCondition;

    private BigDecimal pointPolicyRedemptionRate;

    private LocalDate pointPolicyCreatedAt;

    private LocalDate pointPolicyUpdatedAt;
}
