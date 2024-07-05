package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @NotNull(message = "장바구니 생성일은 필수입니다.")
    @Column(nullable = false)
    private LocalDate cartCreatedAt;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, name = "user_id")
    private Customer customer;

    @Builder
    public Cart(Long cartId, LocalDate cartCreatedAt, Customer customer) {
        this.cartId = cartId;
        this.cartCreatedAt = cartCreatedAt;
        this.customer = customer;
    }
}
