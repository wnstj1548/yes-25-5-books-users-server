package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserGradeService;
import com.yes255.yes255booksusersserver.common.exception.UserStateNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import com.yes255.yes255booksusersserver.persistance.domain.UserTotalAmount;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserGradeRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserStateRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserTotalAmountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class UserGradeServiceImpl implements UserGradeService {

    final private JpaUserGradeRepository userGradeRepository;
    final private JpaUserRepository userRepository;
    final private JpaUserStateRepository userStateRepository;
    final private JpaUserTotalAmountRepository totalAmountRepository;

    // 회원 등급 갱신 체크
    @Override
    public void checkUserGradeUpgrade() {

        UserState userState = userStateRepository.findByUserStateName("ACTIVE");

        if (Objects.isNull(userState)) {
            throw new UserStateNotFoundException(ErrorStatus.toErrorStatus("회원 상태가 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        List<User> users = userRepository.findAllByUserState(userState);

        for (User user : users) {



            // todo : 3개월 이내의 누적 금액 계산 (테이블 수정 필요)
        }
    }
}

// todo : if 누적 금액 미충족, 등급 갱신일(매월 1일)로부터 3개월이 지나면 회원 등급 하향 갱신
// todo : 3개월 이내 누적 금액 충족 시 익월 1일에 회원 등급 상향 갱신

/*
    - 등급 상향 로직
    1. 매달 1일 00:00 3개월 이내의 누적 금액을 계산
    2. 등급에 맞는 누적 금액 충족 시 등급 상향

    - 등급 하향 로직
    1. 매달 1일 3개월 이내 기존 등급에 맞는 누적 금액을 달성하지 못할 시, 누적 금액에 해당하는 등급으로 조정
 */
