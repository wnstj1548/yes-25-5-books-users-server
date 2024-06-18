package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class User {

    @Id
    @Column(unique = true)
    private Long userId;

    @MapsId
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private Customer customer;

    @NotNull(message = "유저 이름은 필수입니다.")
    @Column(nullable = false, length = 50)
    private String userName;

    @NotNull(message = "유저 전화번호는 필수입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다. 010-1234-5678 형식을 따라야 합니다.")
    @Column(nullable = false, length = 15)
    private String userPhone;

    @NotNull(message = "유저 이메일은 필수입니다.")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "유효한 이메일 형식이 아닙니다. yes255@shop.net 형식을 따라야 합니다.")
    @Column(unique = true, nullable = false, length = 100)
    private String userEmail;

    private LocalDate userBirth;

    @NotNull
    private LocalDateTime userRegisterDate;

    private LocalDateTime userLastLoginDate;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "provider_id")
    private Provider provider;

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
    public User(Customer customer, String userName, String userPhone, String userEmail, LocalDate userBirth,
                LocalDateTime userRegisterDate, LocalDateTime userLastLoginDate,
                Provider provider, UserGrade userGrade, UserState userState, String userPassword) {

        this.customer = customer;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userBirth = userBirth;
        this.userRegisterDate = LocalDateTime.now();
        this.provider = provider;
        this.userGrade = userGrade;
        this.userState = userState;
        this.userPassword = userPassword;
    }

    // 마지막 로그인 날짜 갱신
    public void updateLastLoginDate() {
        this.userLastLoginDate = LocalDateTime.now();
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }

    public void updateUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void updateUserBirth(LocalDate userBirth) {
        this.userBirth = userBirth;
    }

    public void updateUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return userName + ", " + userPhone + ", " + userEmail + ", " + userBirth + ", "
                + userRegisterDate + ", " + userLastLoginDate + ", " + provider + ", "
                + userGrade + ", " + userState + ", " + userPassword;
    }
}
