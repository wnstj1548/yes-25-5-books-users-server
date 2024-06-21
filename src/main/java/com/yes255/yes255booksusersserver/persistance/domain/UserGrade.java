package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userGradeId;

    @NotNull(message = "회원 등급명은 필수입니다.")
    @Column(nullable = false, length = 10)
    private String userGradeName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "point_policy_id")
    private PointPolicy pointPolicy;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Builder
    public UserGrade(Long userGradeId, String userGradeName, PointPolicy pointPolicy, User user) {
        this.userGradeId = userGradeId;
        this.userGradeName = userGradeName;
        this.pointPolicy = pointPolicy;
        this.user = user;
    }

    public void updatePointPolicy() {
        this.pointPolicy = null;
    }
}
