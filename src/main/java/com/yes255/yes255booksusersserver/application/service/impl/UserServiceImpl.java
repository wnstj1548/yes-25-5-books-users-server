package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import com.yes255.yes255booksusersserver.application.service.InactiveStateService;
import com.yes255.yes255booksusersserver.application.service.UserService;
import com.yes255.yes255booksusersserver.application.service.queue.producer.MessageProducer;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.ProviderException;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.common.exception.UserGradeException;
import com.yes255.yes255booksusersserver.common.exception.UserStateException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import com.yes255.yes255booksusersserver.persistance.domain.Point;
import com.yes255.yes255booksusersserver.persistance.domain.PointLog;
import com.yes255.yes255booksusersserver.persistance.domain.PointPolicy;
import com.yes255.yes255booksusersserver.persistance.domain.Provider;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserGrade;
import com.yes255.yes255booksusersserver.persistance.domain.UserGradeLog;
import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import com.yes255.yes255booksusersserver.persistance.domain.UserTotalPureAmount;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointLogRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointPolicyRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaProviderRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeLogRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserStateRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserTotalPureAmountRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.user.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.FindUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.LoginUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UnlockDormantRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UpdateUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UserResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final JpaUserRepository userRepository;
    private final JpaCustomerRepository customerRepository;
    private final JpaProviderRepository providerRepository;
    private final JpaUserGradeRepository userGradeRepository;
    private final JpaUserStateRepository userStateRepository;
    private final JpaPointPolicyRepository pointPolicyRepository;
    private final JpaPointRepository pointRepository;
    private final JpaUserGradeLogRepository userGradeLogRepository;
    private final JpaPointLogRepository pointLogRepository;
    private final JpaUserTotalPureAmountRepository userTotalPureAmountRepository;

    private final InactiveStateService inactiveStateService;

    private final PasswordEncoder passwordEncoder;

    private final MessageProducer messageProducer;

    // 로그인을 위한 정보 반환
    @Transactional
    @Override
    public LoginUserResponse findLoginUserByEmailByPassword(LoginUserRequest userRequest) {

        User user = userRepository.findByUserEmail(userRequest.email());

        if (Objects.isNull(user)) {
            throw new UserException(ErrorStatus.toErrorStatus("회원이 존재 하지 않습니다.", 403, LocalDateTime.now()));
        }

        if (!passwordEncoder.matches(userRequest.password(), user.getUserPassword())) {
            throw new UserException(ErrorStatus.toErrorStatus("비밀번호가 일치하지 않습니다.", 403, LocalDateTime.now()));
        }

        if (user.getUserState().getUserStateName().equals("WITHDRAWAL")) {
            throw new UserException(ErrorStatus.toErrorStatus("탈퇴한 회원입니다.", 403, LocalDateTime.now()));
        }

        if (user.getUserState().getUserStateName().equals("INACTIVE")) {
            throw new ApplicationException(
                    ErrorStatus.toErrorStatus("회원이 휴면처리되었습니다.", 403, LocalDateTime.now()));
        }

        if ((user.getUserLastLoginDate() != null && user.getUserLastLoginDate().isBefore(LocalDateTime.now().minusMonths(3)))) {
            inactiveStateService.updateInActiveState(user.getUserId());
            throw new ApplicationException(
                    ErrorStatus.toErrorStatus("회원이 휴면처리되었습니다.", 403, LocalDateTime.now()));
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
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재 하지 않습니다.", 403, LocalDateTime.now())));

        return UserResponse.fromUser(user);
    }

    // 이메일과 전화번호로 유저 이메일 찾기
    @Transactional(readOnly = true)
    @Override
    public List<FindUserResponse> findAllUserEmailByUserNameByUserPhone(FindEmailRequest emailRequest) {

        List<User> users = userRepository.findAllByUserNameAndUserPhone(emailRequest.name(), emailRequest.phone());

        if (Objects.isNull(users)) {
            throw new UserException(ErrorStatus.toErrorStatus("회원이 존재 하지 않습니다.", 403, LocalDateTime.now()));
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
            throw new UserException(ErrorStatus.toErrorStatus("이미 사용중인 이메일입니다.", 403, LocalDateTime.now()));
        }

        // 비밀번호 검증 오류
        if (!userRequest.userPassword().equals(userRequest.userConfirmPassword())) {
            throw new UserException(ErrorStatus.toErrorStatus("비밀번호가 일치하지 않습니다.", 403, LocalDateTime.now()));
        }

        // 회원 가입 시 고객 ID(권한) 부여
        Customer customer = customerRepository.save(Customer.builder()
                .userRole("MEMBER")
                .build());

        // Local 제공자
        Provider provider = providerRepository.findByProviderName(userRequest.providerName());  // 페이코 회원 가입을 고려해 providerName으로 변경

        if (Objects.isNull(provider)) {
            throw new ProviderException(ErrorStatus.toErrorStatus("제공자가 존재 하지 않습니다.", 400, LocalDateTime.now()));
        }

        // 회원 상태 Active
        UserState userState = userStateRepository.findByUserStateName("ACTIVE");

        if (Objects.isNull(userState)) {
            throw new UserStateException(ErrorStatus.toErrorStatus("회원 상태가 존재 하지 않습니다.", 403, LocalDateTime.now()));
        }

        // 회원 등급 NORMAL 부여
        UserGrade userGrade = userGradeRepository.findByUserGradeName("NORMAL");

        if (Objects.isNull(userGrade)) {
            throw new UserGradeException(ErrorStatus.toErrorStatus("회원 등급이 존재 하지 않습니다.", 403, LocalDateTime.now()));
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

        // 최초 회원 순수 주문 금액 이력 작성
        userTotalPureAmountRepository.save(UserTotalPureAmount.builder()
                .userTotalPureAmount(BigDecimal.ZERO)
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

            pointLogRepository.save(PointLog.builder()
                    .point(point)
                    .pointLogUpdatedType(singUpPolicy.getPointPolicyCondition())
                    .pointLogAmount(singUpPolicy.getPointPolicyApplyAmount())
                    .pointLogUpdatedAt(LocalDateTime.now())
                    .build());
        }

        messageProducer.sendWelcomeCouponMessage(user.getUserId());

        // 생일이 오늘이면 생일 쿠폰 발급 메시지 전송
        LocalDate today = LocalDate.now();
        LocalDate userBirth = user.getUserBirth();

        log.info("User's birth date: {}", userBirth);
        log.info("Today's date: {}", today);
        log.info("User birth month and day: {}-{}", userBirth.getMonthValue(), userBirth.getDayOfMonth());
        log.info("Today's month and day: {}-{}", today.getMonthValue(), today.getDayOfMonth());

        if (userBirth.getMonthValue() == today.getMonthValue() && userBirth.getDayOfMonth() == today.getDayOfMonth()) {
            log.info("User's birthday is today. Sending birthday coupon message.");
            messageProducer.sendBirthdayCouponMessage(user.getUserId());
        } else {
            log.info("User's birthday is not today.");
        }

        return UserResponse.fromUser(user);
    }

    // 회원 수정
    @Transactional
    @Override
    public UpdateUserResponse updateUser(Long userId, UpdateUserRequest userRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 403, LocalDateTime.now())));

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
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 403, LocalDateTime.now())));

        if (passwordEncoder.matches(userRequest.userPassword(), user.getUserPassword())) {

            // 회원 상태를 ACTIVE(활성) -> WITHDRAWAL(탈퇴) 전환
            UserState userState = userStateRepository.findByUserStateName("WITHDRAWAL");

            if (Objects.isNull(userState)) {
                throw new UserGradeException(ErrorStatus.toErrorStatus("회원 상태가 존재하지 않습니다.", 403, LocalDateTime.now()));
            }

            user.updateUserState(userState);
            userRepository.save(user);

        }
        else {
            throw new UserException(ErrorStatus.toErrorStatus("비밀번호가 일치하지 않습니다.", 400, LocalDateTime.now()));
        }
    }

    // 비밀번호 확인
    @Transactional
    @Override
    public boolean checkUserPassword(Long userId, CheckPasswordRequest passwordRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재 하지 않습니다.", 403, LocalDateTime.now())));

        // 비밀번호 검증 오류
        return passwordEncoder.matches(passwordRequest.password(), user.getUserPassword());
    }

    // 최근 로그인 날짜 갱신 (사용 안할 수도)
    @Transactional
    @Override
    public void updateLastLoginDate(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재하지 않습니다.", 403, LocalDateTime.now())));

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
                .orElseThrow(() -> new UserException(ErrorStatus.toErrorStatus("회원이 존재 하지 않습니다.", 403, LocalDateTime.now())));

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

    @Override
    public void unLockDormantStateByEmail(UnlockDormantRequest request) {
        User user = userRepository.findByUserEmail(request.email());
        UserState userState = userStateRepository.findByUserStateName("ACTIVE");

        user.updateLastLoginDate();
        user.updateUserState(userState);
    }

}