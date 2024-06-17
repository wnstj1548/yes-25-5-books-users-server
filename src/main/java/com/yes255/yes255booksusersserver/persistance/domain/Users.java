package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {

    @Id
    private Long userId;

    @MapsId
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private Customers customers;

    @NotNull(message = "유저 이름은 필수입니다.")
    @Column(nullable = false, length = 50)
    private String userName;

    @NotNull(message = "유저 전화번호는 필수입니다.")
    @Column(nullable = false, length = 15)
    private String userPhone;

    @NotNull(message = "유저 이메일은 필수입니다.")
    @Column(unique = true, nullable = false, length = 100)
    private String userEmail;

    private LocalDate userBirth;

    @NotNull
    private LocalDateTime userRegisterDate;

    private LocalDateTime userLastLoginDate;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "provider_id")
    private Providers providers;

    @ManyToOne
    @JoinColumn(name = "user_grade_id")
    private UserGrade userGrade;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "user_state_id")
    private UserState userState;

    @NotNull(message = "유저 비밀번호는 필수입니다.")
    @Column(nullable = false)
    private String userPassword;

    // 회원 등록 생성자 (전부)
    @Builder
    public Users(Customers customers, String userName, String userPhone, String userEmail, LocalDate userBirth,
                 Providers providers, UserGrade userGrade, UserState userState, String userPassword) {

        this.customers = customers;
        this.userName = userName;
        this.userPhone = Objects.nonNull(userPhone) ? userPhone : null;
        this.userEmail = userEmail;
        this.userBirth = userBirth;
        this.userRegisterDate = LocalDateTime.now();
        this.providers = providers;
        this.userGrade = userGrade;
        this.userState = userState;
        this.userPassword = userPassword;
    }

    // 회원 등록 생성자 (선택)
    @Builder
    public Users(Customers customers, String userName, String userPhone, String userEmail,
                 Providers providers, UserState userState) {

        this.customers = customers;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userRegisterDate = LocalDateTime.now();
        this.providers = providers;
        this.userState = userState;
    }

    // 마지막 로그인 날짜 갱신
    public void updateLastLoginDate() {
        this.userLastLoginDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return userName + ", " + userPhone + ", " + userEmail + ", " + userBirth + ", "
                + userRegisterDate + ", " + userLastLoginDate + ", " + providers + ", "
                + userGrade + ", " + userState + ", " + userPassword;
    }
}
