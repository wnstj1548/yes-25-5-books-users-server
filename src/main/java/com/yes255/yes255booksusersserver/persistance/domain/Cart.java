package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private User user;

    @Builder
    public Cart(Long cartId, LocalDate cartCreatedAt, User user) {
        this.cartId = cartId;
        this.cartCreatedAt = cartCreatedAt;
        this.user = user;
    }
}
