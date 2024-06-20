package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.UserTotalAmount;

public interface UserTotalAmountService {

    UserTotalAmount findUserTotalAmountByUserId(Long userId);
}
