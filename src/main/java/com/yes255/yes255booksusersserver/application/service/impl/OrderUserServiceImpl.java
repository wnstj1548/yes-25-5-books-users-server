package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.OrderUserService;
import com.yes255.yes255booksusersserver.common.exception.CustomerException;
import com.yes255.yes255booksusersserver.common.exception.PointException;
import com.yes255.yes255booksusersserver.common.exception.UserAddressException;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import com.yes255.yes255booksusersserver.persistance.domain.Point;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateRefundRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

// 주문 유저에게 반환하는 클래스
@Service
@RequiredArgsConstructor
public class OrderUserServiceImpl implements OrderUserService {

    private final JpaPointRepository pointRepository;
    private final JpaUserRepository userRepository;
    private final JpaCustomerRepository customerRepository;
    private final JpaUserAddressRepository userAddressRepository;

    @Transactional(readOnly = true)
    @Override
    public ReadOrderUserInfoResponse orderUserInfo(Long userId) {
        Customer customer = customerRepository.findById(userId)
            .orElseThrow(() -> new CustomerException(
                ErrorStatus.toErrorStatus("고객을 찾을 수 없습니다. 고객 ID : " + userId, 404, LocalDateTime.now())
            ));

        if (customer.getUserRole().equals("NONE_MEMBER")) {
            return ReadOrderUserInfoResponse.fromNoneMember(customer);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 404, LocalDateTime.now())));

        Point point = pointRepository.findByUser_UserId(userId);

        if (Objects.isNull(point)) {
            throw new PointException(ErrorStatus.toErrorStatus("포인트가 존재하지 않습니다.", 404, LocalDateTime.now()));
        }

        return ReadOrderUserInfoResponse.builder()
                .userId(userId)
                .name(user.getUserName())
                .email(user.getUserEmail())
                .phoneNumber(user.getUserPhone())
                .role(user.getCustomer().getUserRole())
                .points(point.getPointCurrent().intValue())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ReadOrderUserAddressResponse> getUserAddresses(Long userId, Pageable pageable) {

        Page<UserAddress> userAddressPage = userAddressRepository.findByUserUserId(userId, pageable);

        if (userAddressPage.isEmpty()) {
            throw new UserAddressException(ErrorStatus.toErrorStatus("유저 주소를 찾을 수 없습니다.", 400, LocalDateTime.now()));
        }

        return userAddressPage.map(userAddress ->
                ReadOrderUserAddressResponse.builder()
                        .userAddressId(userAddress.getUserAddressId())
                        .addressRaw(userAddress.getAddress().getAddressRaw())
                        .addressDetail(userAddress.getAddressDetail())
                        .addressName(userAddress.getAddressName())
                        .zipCode(userAddress.getAddress().getAddressZip())
                        .addressBased(userAddress.isAddressBased())
                        .build()
        );
    }

    @Override
    public ReadUserInfoResponse getUserInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

        Point point = pointRepository.findByUser_UserId(userId);

        if (Objects.isNull(point)) {
            throw new PointException(ErrorStatus.toErrorStatus("포인트가 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        return ReadUserInfoResponse.builder()
                .gradeId(user.getUserGrade().getUserGradeId())
                .points(point.getPointCurrent().intValue())
                .build();
    }
}
