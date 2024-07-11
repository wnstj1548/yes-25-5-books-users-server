package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateAddressBasedRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UpdateUserAddressResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserAddressService {

    CreateUserAddressResponse createAddress(Long userId, CreateUserAddressRequest addressRequest);

    UpdateUserAddressResponse updateAddress(Long userId, Long userAddressId, UpdateUserAddressRequest addressRequest);

    UserAddressResponse findAddressById(Long userId, Long userAddressId);

    Page<UserAddressResponse> findAllAddresses(Long userId, Pageable pageable);

    void deleteAddress(Long userId, Long userAddressId);

    void updateAddressBased(Long userId, Long userAddressId, UpdateAddressBasedRequest addressRequest);
}
