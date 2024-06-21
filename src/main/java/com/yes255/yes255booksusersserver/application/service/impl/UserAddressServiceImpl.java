//package com.yes255.yes255booksusersserver.application.service.impl;
//
//import com.yes255.yes255booksusersserver.application.service.UserAddressService;
//import com.yes255.yes255booksusersserver.persistance.domain.Address;
//import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
//import com.yes255.yes255booksusersserver.persistance.repository.JpaUserAddressRepository;
//import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
//import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserAddressRequest;
//import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateUserAddressRequest;
//import com.yes255.yes255booksusersserver.presentation.dto.response.CreateUserAddressResponse;
//import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateUserAddressResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//@Slf4j
//public class UserAddressServiceImpl implements UserAddressService {
//
//    private final JpaUserAddressRepository userAddressRepository;
//    private final JpaAddressRepository addressRepository;
//
//    @Transactional
//    @Override
//    public CreateUserAddressResponse createAddress(CreateUserAddressRequest addressRequest) {
//
//        UserAddress userAddress = userAddressRepository.save(UserAddress.builder()
//                        .addressName(addressRequest.addressName())
//                        .addressDetail(addressRequest.addressDetail())
//                        .addressBased(addressRequest.addressBased())
//                        .build());
//
//        return CreateUserAddressResponse.builder()
//                .addressName(userAddress.getAddressName())
//                .addressDetail(userAddress.getAddressDetail())
//                .addressBased(userAddress.isAddressBased())
//                .build();
//    }
//
//    @Transactional
//    @Override
//    public UpdateUserAddressResponse updateAddress(Long addressId, UpdateUserAddressRequest addressRequest) {
//
//        UserAddress existingUserAddress = userAddressRepository.findById(addressId)
//                .orElseThrow(() -> new RuntimeException("User Address not found"));
//
//        Address address = addressRepository.findById(addressRequest.addressId())
//                .orElseThrow(() -> new RuntimeException("Address Not Found"));
//
//        // 기존 사용자 주소 정보를 기반으로 새로운 사용자 주소 객체 생성
//        UserAddress updatedUserAddress = UserAddress.builder()
//                .userAddressId(existingUserAddress.getUserAddressId())
//                .addressName(addressRequest.addressName())
//                .addressDetail(addressRequest.addressDetail())
//                .addressBased(addressRequest.addressBased())
//                .address(address)
//                .build();
//
//        return userAddressRepository.save(updatedUserAddress);
//    }
//
//    @Override
//    public UserAddress getAddressById(Long userAddressId) {
//        return userAddressRepository.findById(userAddressId)
//                .orElseThrow(() -> new RuntimeException("User Address not found"));
//    }
//
//    @Override
//    public List<UserAddress> getAllAddresses() {
//        return userAddressRepository.findAll();
//    }
//
//    @Transactional
//    @Override
//    public void deleteAddress(Long userAddressId) {
//        userAddressRepository.deleteById(userAddressId);
//    }
//}
