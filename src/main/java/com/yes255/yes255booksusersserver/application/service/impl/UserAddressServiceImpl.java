package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserAddressRepository;
import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateUserAddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    private final JpaUserAddressRepository userAddressRepository;
    private final JpaAddressRepository addressRepository;
    private final JpaUserRepository userRepository;

    @Transactional
    @Override
    public CreateUserAddressResponse createAddress(Long userId,
                                                   Long addressId,
                                                   CreateUserAddressRequest addressRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));

        UserAddress userAddress = userAddressRepository.save(UserAddress.builder()
                        .addressName(addressRequest.addressName())
                        .addressDetail(addressRequest.addressDetail())
                        .addressBased(addressRequest.addressBased())
                        .address(address)
                        .user(user)
                        .build());

        return CreateUserAddressResponse.builder()
                .addressName(userAddress.getAddressName())
                .addressDetail(userAddress.getAddressDetail())
                .addressBased(userAddress.isAddressBased())
                .build();
    }

    @Transactional
    @Override
    public UpdateUserAddressResponse updateAddress(Long userId,
                                                   Long addressId,
                                                   Long userAddressId,
                                                   UpdateUserAddressRequest addressRequest) {

        UserAddress existingUserAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new RuntimeException("User Address not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address Not Found"));

        // 기존 사용자 주소 정보를 기반으로 새로운 사용자 주소 객체 생성
        UserAddress updatedUserAddress = UserAddress.builder()
                .userAddressId(existingUserAddress.getUserAddressId())
                .addressName(addressRequest.addressName())
                .addressDetail(addressRequest.addressDetail())
                .addressBased(addressRequest.addressBased())
                .address(address)
                .user(existingUserAddress.getUser())
                .build();
        userAddressRepository.save(updatedUserAddress);

        return UpdateUserAddressResponse.builder()
                .addressName(updatedUserAddress.getAddressName())
                .addressDetail(updatedUserAddress.getAddressDetail())
                .addressBased(updatedUserAddress.isAddressBased())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserAddressResponse findAddressById(Long userId,
                                              Long addressId,
                                              Long userAddressId) {

        UserAddress userAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new RuntimeException("User Address not found"));

        return UserAddressResponse.builder()
                .userAddressID(userAddressId)
                .addressId(userAddress.getAddress().getAddressId())
                .addressName(userAddress.getAddressName())
                .addressDetail(userAddress.getAddressDetail())
                .addressBased(userAddress.isAddressBased())
                .userId(userAddress.getUser().getUserId())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserAddressResponse> findAllAddresses(Long userId, Long addressId) {

        List<UserAddress> userAddressList = userAddressRepository.findAll();

        return userAddressList.stream()
                .map(userAddress -> UserAddressResponse.builder()
                        .userAddressID(userAddress.getUserAddressId())
                        .addressId(userAddress.getAddress().getAddressId())
                        .addressName(userAddress.getAddressName())
                        .addressDetail(userAddress.getAddressDetail())
                        .addressBased(userAddress.isAddressBased())
                        .userId(userAddress.getUser().getUserId())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteAddress(Long userId, Long addressId, Long userAddressId) {
        userAddressRepository.deleteById(userAddressId);
    }
}
