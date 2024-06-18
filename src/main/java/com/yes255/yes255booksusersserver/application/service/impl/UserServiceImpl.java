package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserService;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.FindUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.LoginUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JpaUserRepository userRepository;

    private final JpaCustomerRepository customerRepository;
    private final JpaProviderRepository providerRepository;
    private final JpaUserGradeRepository userGradeRepository;
    private final JpaUserStateRepository userStateRepository;

    @Transactional(readOnly = true)
    @Override
    public LoginUserResponse findLoginUserByEmail(LoginUserRequest userRequest) {

        User user = userRepository.findByUserEmail(userRequest.email());

        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("고객 ID가 존재 하지 않습니다.");
        }

        return LoginUserResponse.builder()
                .email(user.getUserEmail())
                .password(user.getUserPassword())
                .userRole(user.getCustomer().getUserRole())
                .loginStatusName(user.getUserState().getUserStateName())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UpdateUserResponse findUserByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(userId + ": 고객 ID가 존재 하지 않습니다."));

        return UpdateUserResponse.builder()
                .userName(user.getUserName())
                .userPhone(user.getUserPhone())
                .userBirth(user.getUserBirth())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<FindUserResponse> findAllUserEmailByUserNameByUserPhone(FindEmailRequest emailRequest, Pageable pageable) {

        List<User> users = userRepository.findAllByUserNameAndUserPhone(emailRequest.name(), emailRequest.phone(), pageable);

        if (Objects.isNull(users)) {
            throw new IllegalArgumentException("회원이 존재 하지 않습니다.");
        }

        return users.stream()
                .map(user -> FindUserResponse.builder()
                        .userEmail(user.getUserEmail())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserResponse createUser(CreateUserRequest userRequest) {

        User checkUser = userRepository.findByUserEmail(userRequest.userEmail());

        if (Objects.nonNull(checkUser)) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        // 비밀번호 검증 오류
        if (!userRequest.userPassword().equals(userRequest.userConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 다릅니다.");
        }

        // 회원 가입 시 고객 ID 부여
        Customer customer = customerRepository.save(Customer.builder()
                                                            .userRole("Member")
                                                            .build());

        Provider provider = providerRepository.findByProviderName("Local");

        UserGrade userGrade = userGradeRepository.findByUserGradeName("Normal");

        UserState userState = userStateRepository.findByUserStateName("Active");

        User user = userRequest.toEntity(customer, provider, userGrade, userState);
        userRepository.save(user);

        log.info("User : {}", user);

        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userPhone(user.getUserPhone())
                .userEmail(user.getUserEmail())
                .userRegisterDate(user.getUserRegisterDate())
                .userLastLoginDate(user.getUserLastLoginDate())
                .providerId(user.getProvider().getProviderId())
                .userGradeId(user.getUserGrade().getUserGradeId())
                .userStateId(user.getUserState().getUserStateId())
                .userPassword(user.getUserPassword())
                .build();
    }

    @Transactional
    @Override
    public UpdateUserResponse updateUser(Long userId, UpdateUserRequest userRequest) {

        // 비밀번호 검증 오류
        if (!userRequest.userPassword().equals(userRequest.userConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 다릅니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(userId + ": 고객 ID가 존재하지 않습니다."));


        user.updateUserName(userRequest.userName());
        user.updateUserPhone(userRequest.userPhone());
        user.updateUserBirth(userRequest.userBirth());
        user.updateUserPassword(userRequest.userPassword());

        userRepository.save(user);

        return UpdateUserResponse.builder()
                .userName(user.getUserName())
                .userPhone(user.getUserPhone())
                .userBirth(user.getUserBirth())
                .build();
    }

    @Transactional
    @Override
    public void deleteUser(Long userId, DeleteUserRequest userRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(userId + ": 고객 ID가 존재 하지 않습니다."));

        if (user.getUserPassword().equals(userRequest.userPassword())) {
            userRepository.delete(user);
        }
    }

    @Transactional
    @Override
    public void updateLastLoginDate(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(userId + ": 고객 ID가 존재 하지 않습니다."));

        user.updateLastLoginDate();
        userRepository.save(user);
    }

    @Override
    public boolean loginUserByEmailByPassword(LoginUserRequest loginUserRequest) {

        User user = userRepository.findByUserEmailAndUserPassword(loginUserRequest.email(), loginUserRequest.password());

        return !Objects.isNull(user);
    }

    @Override
    public boolean findUserPasswordByEmailByName(FindPasswordRequest passwordRequest) {

        User user = userRepository.findByUserEmailAndUserName(passwordRequest.email(), passwordRequest.name());

        return !Objects.isNull(user);
    }

    // todo : 비밀번호 찾기 서비스 작성
    @Override
    public boolean setUserPasswordByUserId(UpdatePasswordRequest passwordRequest) {

        // todo : userId는 어디서 받는지?
//        User user = userRepository.findById()

        return false;
    }

















    private final JpaPointPolicyRepository pointPolicyRepository;
    @Override
    public void createRecord() {

        // DB에 저장 예정
        // -----------------------------------------------------------------------------------------------------
        // 회원 가입 포인트 정책 생성
        PointPolicy pointPolicy = pointPolicyRepository.save(PointPolicy.builder()
                .pointPolicyName("회원 가입 기념 포인트 정책")
                .pointPolicyCondition("회원가입")
                .pointPolicyApplyAmount(BigDecimal.valueOf(5000))
                .pointPolicyApplyType(true)
                .pointPolicyCreatedAt(LocalDate.now())
                .build());

        // Local 제공자 생성
        providerRepository.save(Provider.builder()
                .providerName("Local")
                .build());


        // Normal 회원 등급 생성
        userGradeRepository.save(UserGrade.builder()
                .userGradeName("Normal")
                .pointPolicy(pointPolicy)
                .build());

        // Active 회원 상태 생성
        userStateRepository.save(UserState.builder()
                .userStateName("Active")
                .build());
        // -----------------------------------------------------------------------------------------------------
    }

}
