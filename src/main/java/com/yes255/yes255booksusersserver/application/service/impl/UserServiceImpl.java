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
    private final JpaCartRepository cartRepository;
    private final JpaPointPolicyRepository pointPolicyRepository;
    private final JpaPointRepository pointRepository;
    private final JpaUserTotalAmountRepository totalAmountRepository;

    // 로그인을 위한 정보 반환
    @Transactional(readOnly = true)
    @Override
    public LoginUserResponse findLoginUserByEmail(LoginUserRequest userRequest) {

        User user = userRepository.findByUserEmail(userRequest.email());

        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("고객 ID가 존재 하지 않습니다.");
        }

        // 최근 로그인 날짜 업데이트
        user.updateLastLoginDate();
        userRepository.save(user);

        return LoginUserResponse.builder()
                .userId(user.getUserId())
                .userRole(user.getCustomer().getUserRole())
                .loginStatusName(user.getUserState().getUserStateName())
                .build();
    }

    // 특정 유저 조회
    @Transactional(readOnly = true)
    @Override
    public UserResponse findUserByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(userId + ": 유저 ID가 존재 하지 않습니다."));

        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userPhone(user.getUserPhone())
                .userEmail(user.getUserEmail())
                .userRegisterDate(user.getUserRegisterDate())
                .userLastLoginDate(user.getUserLastLoginDate())
                .providerId(user.getProvider().getProviderId())
                .userStateId(user.getUserState().getUserStateId())
                .userGradeId(user.getUserGrade().getUserGradeId())
                .userPassword(user.getUserPassword())
                .build();
    }

    // 이메일과 전화번호로 유저 이메일 찾기
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


    // 회원 가입
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

        // 회원 가입 시 고객 ID(권한) 부여
        Customer customer = customerRepository.save(Customer.builder()
                                                            .userRole("MEMBER")
                                                            .build());

        // Local 제공자
        Provider provider = providerRepository.findByProviderName("LOCAL");

        // 회원 상태 Active
        UserState userState = userStateRepository.findByUserStateName("ACTIVE");

        // 회원 등급 NORMAL 부여
        UserGrade userGrade = userGradeRepository.findByUserGradeName("NORMAL");

        // 유저 저장
        User user = userRequest.toEntity(customer, provider, userState, userGrade);
        userRepository.save(user);

        // 회원 총 구매 금액 테이블 생성
        UserTotalAmount userTotalAmount = totalAmountRepository.save(UserTotalAmount.builder()
                .user(user)
                .build());

        // 회원 장바구니 생성
        Cart cart = cartRepository.save(Cart.builder()
                        .cartCreatedAt(LocalDate.now())
                        .user(user)
                        .build());

        // 회원 포인트 생성
        Point point = pointRepository.save(Point.builder()
                        .pointCurrent(BigDecimal.valueOf(0))
                        .user(user)
                        .build());

        // 만약 회원가입 정책이 존재한다면 회원 가입 포인트 지급
        PointPolicy singUpPolicy = pointPolicyRepository.findByPointPolicyName("SIGN-UP");
        if (Objects.nonNull(singUpPolicy)) {
            point.updatePointCurrent(singUpPolicy.getPointPolicyApplyAmount());
            pointRepository.save(point);
        }

        log.info("User : {}", user);

        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userPhone(user.getUserPhone())
                .userEmail(user.getUserEmail())
                .userRegisterDate(user.getUserRegisterDate())
                .userLastLoginDate(user.getUserLastLoginDate())
                .providerId(user.getProvider().getProviderId())
                .userStateId(user.getUserState().getUserStateId())
                .userGradeId(user.getUserGrade().getUserGradeId())
                .userPassword(user.getUserPassword())
                .build();
    }

    // 회원 수정
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

    // 회원 탈퇴
    @Transactional
    @Override
    public void deleteUser(Long userId, DeleteUserRequest userRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(userId + ": 고객 ID가 존재 하지 않습니다."));

        if (user.getUserPassword().equals(userRequest.userPassword())) {
            userRepository.delete(user);
        }
    }

    // 최근 로그인 날짜 갱신 (사용 안할 수도)
    @Transactional
    @Override
    public void updateLastLoginDate(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(userId + ": 고객 ID가 존재 하지 않습니다."));

        user.updateLastLoginDate();
        userRepository.save(user);
    }

    // 이메일과 비밀번호로 로그인
    @Transactional(readOnly = true)
    @Override
    public boolean loginUserByEmailByPassword(LoginUserRequest loginUserRequest) {

        User user = userRepository.findByUserEmailAndUserPassword(loginUserRequest.email(), loginUserRequest.password());

        if (Objects.nonNull(user)) {
            user.updateLastLoginDate();

            return true;
        }

        return false;
    }

    // 이메일과 이름으로 비밀번호 찾기
    @Transactional(readOnly = true)
    @Override
    public boolean findUserPasswordByEmailByName(FindPasswordRequest passwordRequest) {

        User user = userRepository.findByUserEmailAndUserName(passwordRequest.email(), passwordRequest.name());

        return !Objects.isNull(user);
    }

    // todo : 비밀번호 찾기 서비스 작성
    @Transactional(readOnly = true)
    @Override
    public boolean setUserPasswordByUserId(UpdatePasswordRequest passwordRequest) {

        // todo : userId는 어디서 받는지?
//        User user = userRepository.findById()

        return false;
    }
}
