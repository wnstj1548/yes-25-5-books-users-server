package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UpdateUserAddressResponse;

import java.util.List;

public interface UserAddressService {

    CreateUserAddressResponse createAddress(Long userId, Long addressId, CreateUserAddressRequest addressRequest);

    UpdateUserAddressResponse updateAddress(Long userId, Long addressId, Long userAddressId, UpdateUserAddressRequest addressRequest);

    UserAddressResponse findAddressById(Long userId, Long addressId, Long userAddressId);

    List<UserAddressResponse> findAllAddresses(Long userId, Long addressId);

    void deleteAddress(Long userId, Long addressId, Long userAddressId);
}
