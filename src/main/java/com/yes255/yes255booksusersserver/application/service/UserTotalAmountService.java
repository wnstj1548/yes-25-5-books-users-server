package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.response.UserTotalAmountResponse;

public interface UserTotalAmountService {

    UserTotalAmountResponse findUserTotalAmountByUserId(Long userId);

    void deleteUserTotalAmountByUserId(Long userId);
}
