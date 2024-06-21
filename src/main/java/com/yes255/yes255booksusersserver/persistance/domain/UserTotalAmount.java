package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserTotalAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTotalAmountId;

    @NotNull(message = "유저 누적 금액은 필수입니다.")
    @Column(nullable = false)
    private BigDecimal userTotalAmount;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Builder
    public UserTotalAmount(BigDecimal userTotalAmount, User user) {
        this.userTotalAmount = userTotalAmount;
        this.user = user;
    }

    public void updateTotalAmount(BigDecimal totalAmount) {
        this.userTotalAmount = this.userTotalAmount.add(totalAmount);
    }
}
