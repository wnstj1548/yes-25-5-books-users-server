package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull(message = "유저 권한은 필수입니다.")
    @Column(nullable = false, length = 10)
    private String userRole;

    @Builder
    public Customer(String userRole) {
        this.userRole = userRole;
    }
}
