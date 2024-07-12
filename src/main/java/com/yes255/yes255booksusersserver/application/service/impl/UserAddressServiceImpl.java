package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.PointService;
import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.common.exception.AddressException;
import com.yes255.yes255booksusersserver.common.exception.UserAddressException;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserAddressRepository;
import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateAddressBasedRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UpdateUserAddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    private final JpaUserAddressRepository userAddressRepository;
    private final JpaAddressRepository addressRepository;
    private final JpaUserRepository userRepository;
    private final PointService pointService;

    @Transactional
    @Override
    public CreateUserAddressResponse createAddress(Long userId,
                                                   CreateUserAddressRequest addressRequest) {

        List<UserAddress> userAddresses = userAddressRepository.findByUserUserId(userId);

        if (userAddresses.size() > 10) {
            throw new UserAddressException(ErrorStatus.toErrorStatus("주소는 최대 10개까지 등록할 수 있습니다.", 400, LocalDateTime.now()));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("유저가 존재하지 않습니다.", 400, LocalDateTime.now())));

        Address address = addressRepository.findAddressByAddressRawAndAddressZip(addressRequest.addressRaw(), addressRequest.addressZip());

        if (address == null) {
            address = addressRepository.save(Address.builder()
                            .addressZip(addressRequest.addressZip())
                            .addressRaw(addressRequest.addressRaw())
                            .build());
        }

        if (addressRequest.addressBased()) {
            for (UserAddress userAddressTemp : userAddresses) {
                userAddressTemp.updateUserAddressBased(false);
            }
        }

        UserAddress userAddress = userAddressRepository.save(UserAddress.builder()
                        .addressName(addressRequest.addressName())
                        .addressDetail(addressRequest.addressDetail())
                        .addressBased(addressRequest.addressBased())
                        .address(address)
                        .user(user)
                        .build());

        return CreateUserAddressResponse.builder()
                .addressZip(address.getAddressZip())
                .addressRaw(address.getAddressRaw())
                .addressName(userAddress.getAddressName())
                .addressDetail(userAddress.getAddressDetail())
                .addressBased(userAddress.isAddressBased())
                .build();
    }

    @Transactional
    @Override
    public UpdateUserAddressResponse updateAddress(Long userId,
                                                   Long userAddressId,
                                                   UpdateUserAddressRequest addressRequest) {

        UserAddress userAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new UserAddressException(ErrorStatus.toErrorStatus("유저 주소를 찾을 수 없습니다.", 400, LocalDateTime.now())));

        Address address = addressRepository.findById(userAddress.getAddress().getAddressId())
                .orElseThrow(() -> new AddressException(ErrorStatus.toErrorStatus("주소를 찾을 수 없습니다.", 400, LocalDateTime.now())));

        address.updateAddressZip(addressRequest.addressZip());
        address.updateAddressRaw(addressRequest.addressRaw());

        Address checkAddress = addressRepository.findAddressByAddressRawAndAddressZip(addressRequest.addressRaw(), addressRequest.addressZip());
        if (checkAddress == null) {
            addressRepository.save(address);
            userAddress.updateUserAddress(address);
        }
        else {
            userAddress.updateUserAddress(checkAddress);
        }

        userAddress.updateUserAddressName(addressRequest.addressName());
        userAddress.updateUserAddressDetail(addressRequest.addressDetail());
        userAddress.updateUserAddressBased(addressRequest.addressBased());

        userAddressRepository.save(userAddress);

        return UpdateUserAddressResponse.builder()
                .addressZip(address.getAddressZip())
                .addressRaw(address.getAddressRaw())
                .addressName(userAddress.getAddressName())
                .addressDetail(userAddress.getAddressDetail())
                .addressBased(userAddress.isAddressBased())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserAddressResponse findAddressById(Long userId,
                                              Long userAddressId) {

        UserAddress userAddress = userAddressRepository.findByUserAddressIdAndUserUserId(userAddressId, userId);

        if (Objects.isNull(userAddress)) {
            throw new UserAddressException(ErrorStatus.toErrorStatus("유저 주소를 찾을 수 없습니다.", 400, LocalDateTime.now()));
        }

        return UserAddressResponse.builder()
                .userAddressId(userAddressId)
                .addressId(userAddress.getAddress().getAddressId())
                .addressZip(userAddress.getAddress().getAddressZip())
                .addressRaw(userAddress.getAddress().getAddressRaw())
                .addressName(userAddress.getAddressName())
                .addressDetail(userAddress.getAddressDetail())
                .addressBased(userAddress.isAddressBased())
                .userId(userId)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserAddressResponse> findAllAddresses(Long userId, Pageable pageable) {

        Page<UserAddress> userAddressList = userAddressRepository.findByUserUserId(userId, pageable);

        return userAddressList.map(userAddress -> UserAddressResponse.builder()
                .userAddressId(userAddress.getUserAddressId())
                .addressId(userAddress.getAddress().getAddressId())
                .addressZip(userAddress.getAddress().getAddressZip())
                .addressRaw(userAddress.getAddress().getAddressRaw())
                .addressName(userAddress.getAddressName())
                .addressDetail(userAddress.getAddressDetail())
                .addressBased(userAddress.isAddressBased())
                .userId(userId)
                .build());
    }

    @Transactional
    @Override
    public void deleteAddress(Long userId, Long userAddressId) {
        userAddressRepository.deleteById(userAddressId);
    }

    // 기본 배송지 지정
    @Transactional
    @Override
    public void updateAddressBased(Long userId, Long userAddressId, UpdateAddressBasedRequest addressRequest) {

        UserAddress userAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new UserAddressException(ErrorStatus.toErrorStatus("유저 주소를 찾을 수 없습니다.", 400, LocalDateTime.now())));

        List<UserAddress> userAddresses = userAddressRepository.findAll();

        // 모든 주소의 기본 배송지 false로 변환
        if (userAddresses.isEmpty()) {
            throw new UserAddressException(ErrorStatus.toErrorStatus("유저 주소를 찾을 수 없습니다.", 400, LocalDateTime.now()));
        }

        for (UserAddress userAddressTemp : userAddresses) {
            userAddressTemp.updateUserAddressBased(false);
        }

        // 기본 배송지 지정
        userAddress.updateUserAddressBased(true);
    }
}
