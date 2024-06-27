package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReaderOrderUserInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UpdateUserAddressResponse;

import java.util.List;

public interface UserAddressService {

    CreateUserAddressResponse createAddress(Long userId, CreateUserAddressRequest addressRequest);

    UpdateUserAddressResponse updateAddress(Long userId, Long userAddressId, UpdateUserAddressRequest addressRequest);

    UserAddressResponse findAddressById(Long userId, Long userAddressId);

    List<UserAddressResponse> findAllAddresses(Long userId);

    void deleteAddress(Long userId, Long userAddressId);

    ReaderOrderUserInfoResponse orderUserInfo(Long userId);
}
