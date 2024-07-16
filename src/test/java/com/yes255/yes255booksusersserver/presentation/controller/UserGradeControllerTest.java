package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserGradeService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.presentation.dto.response.usergrade.UserGradeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserGradeControllerTest {

    @Mock
    private UserGradeService userGradeService;

    @InjectMocks
    private UserGradeController userGradeController;

    private JwtUserDetails jwtUserDetails;
    private UserGradeResponse userGradeResponse;

    @BeforeEach
    void setUp() {
        jwtUserDetails = JwtUserDetails.of(1L, "USER", "accessToken", "refreshToken");

        userGradeResponse = UserGradeResponse.builder()
                .userGradeId(1L)
                .userGradeName("Gold")
                .pointPolicyId(1L)
                .pointPolicyCondition("Condition")
                .build();
    }

    @Test
    @DisplayName("특정 회원 등급 조회 - 성공")
    void testGetGrades() {
        when(userGradeService.getUserGrade(jwtUserDetails.userId()))
                .thenReturn(userGradeResponse);

        ResponseEntity<UserGradeResponse> response = userGradeController.getGrades(jwtUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userGradeResponse);
    }
}
