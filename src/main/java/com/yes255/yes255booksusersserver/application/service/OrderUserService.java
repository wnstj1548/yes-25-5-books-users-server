package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateRefundRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadUserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderUserService {

    ReadOrderUserInfoResponse orderUserInfo(Long userId);

    Page<ReadOrderUserAddressResponse> getUserAddresses(Long userId, Pageable pageable);

    ReadUserInfoResponse getUserInfo(Long userId);
}
