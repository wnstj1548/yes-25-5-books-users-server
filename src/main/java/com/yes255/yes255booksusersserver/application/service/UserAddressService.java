package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateUserAddressResponse;

import java.util.List;

public interface UserAddressService {

    CreateUserAddressResponse createAddress(CreateUserAddressRequest addressRequest);

    UpdateUserAddressResponse updateAddress(Long addressId, UpdateUserAddressRequest addressRequest);

    UserAddressResponse getAddressById(Long addressId);

    List<UserAddressResponse> getAllAddresses();

    void deleteAddress(Long addressId);
}
