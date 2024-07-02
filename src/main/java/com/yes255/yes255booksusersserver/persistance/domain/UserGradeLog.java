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
public class UserGradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userGradeLogId;

    @NotNull(message = "회원 등급 변동 일자는 필수입니다.")
    private LocalDate userGradeUpdatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_grade_id")
    private UserGrade userGrade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder
    public UserGradeLog(Long userGradeLogId, LocalDate userGradeUpdatedAt, UserGrade userGrade, User user) {
        this.userGradeLogId = userGradeLogId;
        this.userGradeUpdatedAt = userGradeUpdatedAt;
        this.userGrade = userGrade;
        this.user = user;
    }
}
