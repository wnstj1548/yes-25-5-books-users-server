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
public class PointLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointLogId;

    @NotNull(message = "포인트 변경 날짜는 필수입니다.")
    @Column(nullable = false)
    private LocalDate pointLogUpdatedAt;

    @NotNull(message = "포인트 사용처는 필수입니다.")
    @Column(nullable = false, length = 20)
    private String pointLogUsed;

    @NotNull(message = "포인트 변경 타입은 필수입니다.")
    @Column(nullable = false, length = 20)
    private String pointLogUpdatedType;

    @NotNull(message = "포인트 갱신액은 필수입니다.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pointLogAmount;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "point_id")
    private Point point;

    @Builder
    public PointLog(LocalDate pointLogUpdatedAt, String pointLogUsed, String pointLogUpdatedType, BigDecimal pointLogAmount, Point point) {
        this.pointLogUpdatedAt = pointLogUpdatedAt;
        this.pointLogUsed = pointLogUsed;
        this.pointLogUpdatedType = pointLogUpdatedType;
        this.pointLogAmount = pointLogAmount;
        this.point = point;
    }
}
