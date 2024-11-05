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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserTotalPureAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTotalAmountId;

    @NotNull(message = "유저 누적 금액은 필수입니다.")
    @Column(nullable = false)
    private BigDecimal userTotalPureAmount;

    @NotNull(message = "유저 총 순수금액 기록일은 필수입니다.")
    @Column(nullable = false)
    private LocalDate userTotalPureAmountRecordedAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Builder
    public UserTotalPureAmount(BigDecimal userTotalPureAmount, User user) {
        this.userTotalPureAmount = userTotalPureAmount;
        this.userTotalPureAmountRecordedAt = LocalDate.now();
        this.user = user;
    }
}
