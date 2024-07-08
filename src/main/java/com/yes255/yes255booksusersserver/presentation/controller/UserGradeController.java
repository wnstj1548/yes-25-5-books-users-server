package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserGradeService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.response.usergrade.UserGradeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 등급 API", description = "회원 등급 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserGradeController {

    private final UserGradeService userGradeService;

    @Operation(summary = "회원 등급 조회", description = "특정 회원의 등급을 조회합니다.")
    @GetMapping("/grades")
    public ResponseEntity<UserGradeResponse> getGrades(@CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(userGradeService.getUserGrade(userId));
    }
}
