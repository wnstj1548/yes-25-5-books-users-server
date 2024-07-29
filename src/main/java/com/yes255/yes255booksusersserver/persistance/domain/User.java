package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(unique = true)
    private Long userId;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private Customer customer;

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
    private Provider provider;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "user_state_id")
    private UserState userState;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "user_grade_id")
    private UserGrade userGrade;

    @NotNull(message = "유저 비밀번호는 필수입니다.")
    @Column(nullable = false)
    private String userPassword;

    // 회원 등록 생성자 (전부)
    @Builder
    public User(Long userId, Customer customer, String userName, String userPhone, String userEmail, LocalDate userBirth,
                UserGrade userGrade, LocalDateTime userRegisterDate, LocalDateTime userLastLoginDate,
                Provider provider, UserState userState, String userPassword) {
        this.userId = userId;

        this.customer = customer;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userBirth = userBirth;
        this.userRegisterDate = LocalDateTime.now();
        this.userLastLoginDate = userLastLoginDate;
        this.provider = provider;
        this.userState = userState;
        this.userGrade = userGrade;
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

    public void updateUserGrade(UserGrade userGrade) {
        this.userGrade = userGrade;
    }

    public void updateUserState(UserState userState) {
        this.userState = userState;
    }

    @Override
    public String toString() {
        return userName + ", " + userPhone + ", " + userEmail + ", " + userBirth + ", "
                + userRegisterDate + ", " + userLastLoginDate + ", " + provider + ", "
                + ", " + userState + ", " + userPassword;
    }
}
