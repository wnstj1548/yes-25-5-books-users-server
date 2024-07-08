package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.response.ReadPurePriceResponse;

public interface UserTotalPureAmountService {

    ReadPurePriceResponse findUserTotalPureAmountByUserId(Long userId);

    void deleteUserTotalPureAmountByUserId(Long userId);
}
