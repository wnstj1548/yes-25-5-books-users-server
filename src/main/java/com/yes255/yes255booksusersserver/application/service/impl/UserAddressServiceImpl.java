package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.OrderProductService;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.repository.JpaOrderProductRepository;
import com.yes255.yes255booksusersserver.persistance.domain.OrderProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserAddressRepository;
import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//@RequiredArgsConstructor
//@Service
//@Slf4j
//@Transactional
//public class UserAddressServiceImpl implements UserAddressService {
/**
 *
 *  Address createAddress(Address address);
 *     Address updateAddress(Long addressId, Address address);
 *     Address getAddressById(Long addressId);
 *     List<Address> getAllAddresses();
 *     void deleteAddress(Long addressId);
 */
//
//    private final JpaUserAddressRepository userAddressRepository;
//
//    @Override
//    public UserAddress createUserAddress(UserAddress userAddress) {
//        return userAddressRepository.save(userAddress);
//    }
//
//    @Override
//    public UserAddress updateUserAddress(Long userAddressId, UserAddress userAddress) {
//        UserAddress existingUserAddress = userAddressRepository.findById(userAddressId)
//                .orElseThrow(() -> new RuntimeException("User Address not found"));
//
//        // 기존 사용자 주소 정보를 기반으로 새로운 사용자 주소 객체 생성
//        UserAddress updatedUserAddress = UserAddress.builder()
//                .userAddressId(existingUserAddress.getUserAddressId())
//                .addressId(userAddress.getAddressId() != null ? userAddress.getAddressId() : existingUserAddress.getAddressId())
//                .addressName(userAddress.getAddressName() != null ? userAddress.getAddressName() : existingUserAddress.getAddressName())
//                .addressDetail(userAddress.getAddressDetail() != null ? userAddress.getAddressDetail() : existingUserAddress.getAddressDetail())
//                .addressBased(userAddress.isAddressBased())
//                .address(existingUserAddress.getAddress())
//                .user(existingUserAddress.getUser())
//                .build();
//
//        return userAddressRepository.save(updatedUserAddress);
//    }
//
//    @Override
//    public UserAddress getUserAddressById(Long userAddressId) {
//        return userAddressRepository.findById(userAddressId)
//                .orElseThrow(() -> new RuntimeException("User Address not found"));
//    }
//
//    @Override
//    public List<UserAddress> getAllUserAddresses() {
//        return userAddressRepository.findAll();
//    }
//
//    @Override
//    public void deleteUserAddress(Long userAddressId) {
//        userAddressRepository.deleteById(userAddressId);
//    }
//
//    @Override
//    public UserAddress createUserAddress(UserAddress userAddress) {
//        return userAddressRepository.save(userAddress);
//    }
//
//    @Override
//    public UserAddress createAddress(UserAddress address) {
//        return userAddressRepository.save(address);
//    }
//
//    @Override
//    public UserAddress updateAddress(Long addressId, UserAddress address) {
//        return null;
//    }
//
//    @Override
//    public UserAddress getAddressById(Long addressId) {
//        return null;
//    }
//
//    @Override
//    public List<UserAddress> getAllAddresses() {
//        return List.of();
//    }
//
//    @Override
//    public void deleteAddress(Long addressId) {
//
//    }
//}
