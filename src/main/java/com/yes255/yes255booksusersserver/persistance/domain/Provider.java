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
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;

    @NotNull(message = "제공자명은 필수입니다.")
    @Column(nullable = false, length = 10)
    private String providerName;

    @Builder
    public Provider(Long providerId, String providerName) {
        this.providerId = providerId;
        this.providerName = providerName;
    }
}