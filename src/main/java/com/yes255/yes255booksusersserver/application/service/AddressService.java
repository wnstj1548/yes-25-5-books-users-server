package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.address.AddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.CreateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.UpdateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.AddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.CreateAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.UpdateAddressResponse;

import java.util.List;

public interface AddressService {

    CreateAddressResponse createAddress(CreateAddressRequest addressRequest);

    UpdateAddressResponse updateAddress(Long addressId, UpdateAddressRequest addressRequest);

    AddressResponse findAddressById(Long addressId);

    List<AddressResponse> findAllAddresses();

    void deleteAddress(Long addressId);

    AddressResponse findByAddressZipOrAddressRaw(AddressRequest addressRequest);
}
