package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserGrade;
import com.yes255.yes255booksusersserver.presentation.dto.response.usergrade.UserGradeResponse;

import java.math.BigDecimal;

public interface UserGradeService {

    UserGradeResponse getUserGrade(Long userId);

    void updateUserGrade(User user, BigDecimal purePrices);
}
