package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserAddressRepository;
import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    private final JpaUserAddressRepository userAddressRepository;

    @Transactional
    @Override
    public UserAddress createAddress(UserAddress userAddress) {
        return userAddressRepository.save(userAddress);
    }

    @Transactional
    @Override
    public UserAddress updateAddress(Long addressId, UserAddress userAddress) {
        UserAddress existingUserAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("User Address not found"));

        // 기존 사용자 주소 정보를 기반으로 새로운 사용자 주소 객체 생성
        UserAddress updatedUserAddress = UserAddress.builder()
                .userAddressId(existingUserAddress.getUserAddressId())
                .addressId(userAddress.getAddressId() != null ? userAddress.getAddressId() : existingUserAddress.getAddressId())
                .addressName(userAddress.getAddressName() != null ? userAddress.getAddressName() : existingUserAddress.getAddressName())
                .addressDetail(userAddress.getAddressDetail() != null ? userAddress.getAddressDetail() : existingUserAddress.getAddressDetail())
                .addressBased(userAddress.isAddressBased())
                .address(existingUserAddress.getAddress())
                .user(existingUserAddress.getUser())
                .build();

        return userAddressRepository.save(updatedUserAddress);
    }

    @Override
    public UserAddress getAddressById(Long userAddressId) {
        return userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new RuntimeException("User Address not found"));
    }

    @Override
    public List<UserAddress> getAllAddresses() {
        return userAddressRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteAddress(Long userAddressId) {
        userAddressRepository.deleteById(userAddressId);
    }
}
