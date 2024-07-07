package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.InactiveStateService;
import com.yes255.yes255booksusersserver.application.service.UserService;
import com.yes255.yes255booksusersserver.application.service.queue.producer.MessageProducer;
import com.yes255.yes255booksusersserver.common.exception.*;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.user.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.FindUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.LoginUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UpdateUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final JpaUserGradeLogRepository userGradeLogRepository;
    private final JpaPointLogRepository pointLogRepository;

    private final InactiveStateService inactiveStateService;

    private final PasswordEncoder passwordEncoder;

    private final MessageProducer messageProducer;

    // 로그인을 위한 정보 반환
    @Transactional
    @Override
    public LoginUserResponse findLoginUserByEmailByPassword(LoginUserRequest userRequest) {

        User user = userRepository.findByUserEmail(userRequest.email());

        if (Objects.isNull(user)) {
            throw new UserException(ErrorStatus.toErrorStatus("회원이 존재 하지 않습니다.", 400, LocalDateTime.now()));
        }

        if (user.getUserState().getUserStateName().equals("WITHDRAWAL")) {
            throw new UserException(ErrorStatus.toErrorStatus("탈퇴한 회원입니다.", 400, LocalDateTime.now()));
        }

        if (user.getUserLastLoginDate() != null && user.getUserLastLoginDate().isBefore(LocalDateTime.now().minusMonths(3))) {
            inactiveStateService.updateInActiveState(user.getUserId());
            throw new ApplicationException(
                ErrorStatus.toErrorStatus("회원이 휴면처리되었습니다.", 403, LocalDateTime.now()));
        }

        if (!passwordEncoder.matches(userRequest.password(), user.getUserPassword())) {
            throw new UserException(ErrorStatus.toErrorStatus("비밀번호가 일치하지 않습니다.", 400, LocalDateTime.now()));
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
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재 하지 않습니다.", 400, LocalDateTime.now())));

        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userPhone(user.getUserPhone())
                .userEmail(user.getUserEmail())
                .userBirth(user.getUserBirth())
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
            throw new UserException(ErrorStatus.toErrorStatus("회원이 존재 하지 않습니다.", 400, LocalDateTime.now()));
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
            throw new UserException(ErrorStatus.toErrorStatus("이미 사용중인 이메일입니다.", 400, LocalDateTime.now()));
        }

        // 비밀번호 검증 오류
        if (!userRequest.userPassword().equals(userRequest.userConfirmPassword())) {
            throw new UserException(ErrorStatus.toErrorStatus("비밀번호가 일치하지 않습니다.", 400, LocalDateTime.now()));
        }

        // 회원 가입 시 고객 ID(권한) 부여
        Customer customer = customerRepository.save(Customer.builder()
                .userRole("MEMBER")
                .build());

        // Local 제공자
        Provider provider = providerRepository.findByProviderName("LOCAL");

        if (Objects.isNull(provider)) {
            throw new ProviderException(ErrorStatus.toErrorStatus("제공자가 존재 하지 않습니다.", 400, LocalDateTime.now()));
        }

        // 회원 상태 Active
        UserState userState = userStateRepository.findByUserStateName("ACTIVE");

        if (Objects.isNull(userState)) {
            throw new UserStateException(ErrorStatus.toErrorStatus("회원 상태가 존재 하지 않습니다.", 400, LocalDateTime.now()));
        }

        // 회원 등급 NORMAL 부여
        UserGrade userGrade = userGradeRepository.findByUserGradeName("NORMAL");

        if (Objects.isNull(userGrade)) {
            throw new UserGradeException(ErrorStatus.toErrorStatus("회원 등급이 존재 하지 않습니다.", 400, LocalDateTime.now()));
        }

        // 비밀번호 인코딩
        String encodedPwd = passwordEncoder.encode(userRequest.userPassword());

        // 유저 저장
        User user = User.builder()
                .customer(customer)
                .userName(userRequest.userName())
                .userBirth(userRequest.userBirth())
                .userEmail(userRequest.userEmail())
                .userPhone(userRequest.userPhone())
                .provider(provider)
                .userState(userState)
                .userGrade(userGrade)
                .userPassword(encodedPwd)
                .build();

        userRepository.save(user);

        // 최초 회원 등급 이력 작성
        userGradeLogRepository.save(UserGradeLog.builder()
                .userGradeUpdatedAt(LocalDate.now())
                .userGrade(userGrade)
                .user(user)
                .build());

        // 회원 장바구니 생성
        Cart cart = cartRepository.save(Cart.builder()
                .cartCreatedAt(LocalDate.now())
                .customer(customer)
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

            pointLogRepository.save(PointLog.builder()
                            .point(point)
                            .pointLogUpdatedType(singUpPolicy.getPointPolicyCondition())
                            .pointLogAmount(singUpPolicy.getPointPolicyApplyAmount())
                            .pointLogUpdatedAt(LocalDateTime.now())
                            .build());
        }

        messageProducer.sendWelcomeCouponMessage(user.getUserId());

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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

        if (!passwordEncoder.matches(userRequest.userPassword(), user.getUserPassword())) {
            throw new UserException(ErrorStatus.toErrorStatus("현재 비밀번호가 일치하지 않습니다.", 400, LocalDateTime.now()));
        }

        // 비밀번호 검증 오류
        if (Objects.nonNull(userRequest.newUserPassword()) && Objects.nonNull(userRequest.newUserConfirmPassword())
               && !userRequest.newUserPassword().equals(userRequest.newUserConfirmPassword())) {
            throw new UserException(ErrorStatus.toErrorStatus("새 비밀번호가 일치하지 않습니다.", 400, LocalDateTime.now()));
        }
        else if (!userRequest.newUserPassword().isEmpty() && !userRequest.newUserConfirmPassword().isEmpty()
                && userRequest.newUserPassword().equals(userRequest.newUserConfirmPassword())) {
            user.updateUserPassword(passwordEncoder.encode(userRequest.newUserPassword()));
        }

        user.updateUserName(userRequest.userName());
        user.updateUserPhone(userRequest.userPhone());
        user.updateUserBirth(userRequest.userBirth());

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
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

        if (passwordEncoder.matches(userRequest.userPassword(), user.getUserPassword())) {

            // 회원 상태를 ACTIVE(활성) -> WITHDRAWAL(탈퇴) 전환
            UserState userState = userStateRepository.findByUserStateName("WITHDRAWAL");

            if (Objects.isNull(userState)) {
                throw new UserGradeException(ErrorStatus.toErrorStatus("회원 상태가 존재하지 않습니다.", 400, LocalDateTime.now()));
            }

            user.updateUserState(userState);
            userRepository.save(user);

        }
        else {
            throw new UserException(ErrorStatus.toErrorStatus("비밀번호가 일치하지 않습니다.", 400, LocalDateTime.now()));
        }
    }

    // 최근 로그인 날짜 갱신 (사용 안할 수도)
    @Transactional
    @Override
    public void updateLastLoginDate(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 400, LocalDateTime.now())));

        user.updateLastLoginDate();
        userRepository.save(user);
    }

    // 이메일과 비밀번호로 로그인
    @Transactional(readOnly = true)
    @Override
    public boolean loginUserByEmailByPassword(LoginUserRequest loginUserRequest) {

        User user = userRepository.findByUserEmail(loginUserRequest.email());

        if (Objects.nonNull(user) && passwordEncoder.matches(loginUserRequest.password(), user.getUserPassword())) {
            user.updateLastLoginDate();
            userRepository.save(user);

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

    // 비밀번호 재설정
    @Transactional
    @Override
    public boolean setUserPasswordByUserId(Long userId, UpdatePasswordRequest passwordRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재 하지 않습니다.", 400, LocalDateTime.now())));

        // 비밀번호 검증 오류
        if (!passwordRequest.password().equals(passwordRequest.confirmPassword())) {
            throw new UserException(ErrorStatus.toErrorStatus("비밀번호가 일치하지 않습니다.", 400, LocalDateTime.now()));
        }

        user.updateUserPassword(passwordRequest.confirmPassword());

        userRepository.save(user);

        return true;
    }

    // 가입 이메일 중복 확인
    @Override
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByUserEmail(email);
    }

    @Override
    public List<Long> findUserIdsWithBirthdaysInCurrentMonth() {
        int currentMonth = LocalDate.now().getMonthValue();
        return userRepository.findUsersByBirthMonth(currentMonth).stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
    }

}
