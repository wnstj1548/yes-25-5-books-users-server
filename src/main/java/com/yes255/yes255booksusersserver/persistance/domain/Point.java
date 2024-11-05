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
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pointCurrent;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Builder
    public Point(BigDecimal pointCurrent,User user) {
        this.pointCurrent = pointCurrent;
        this.user = user;
    }

    public void updatePointCurrent(BigDecimal pointCurrent) {
        this.pointCurrent = pointCurrent;
    }
}
