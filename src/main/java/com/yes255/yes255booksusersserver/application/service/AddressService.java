package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.presentation.dto.request.AddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.AddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateAddressResponse;

import java.util.List;

public interface AddressService {

    CreateAddressResponse createAddress(CreateAddressRequest addressRequest);

    UpdateAddressResponse updateAddress(Long addressId, UpdateAddressRequest addressRequest);

    AddressResponse findAddressById(Long addressId);

    List<AddressResponse> findAllAddresses();

    void deleteAddress(Long addressId);

    AddressResponse findByAddressZipOrAddressRaw(AddressRequest addressRequest);
}
