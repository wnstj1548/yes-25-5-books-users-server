package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.InactiveStateService;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserStateRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InactiveStateServiceImpl implements InactiveStateService {

    private final JpaUserRepository userRepository;
    private final JpaUserStateRepository userStateRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateInActiveState(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(
                ErrorStatus.toErrorStatus("해당하는 유저를 찾을 수 없습니다. 유저 ID : " + userId, 404, LocalDateTime.now())));

        UserState userState = userStateRepository.findByUserStateName("INACTIVE");
        user.updateUserState(userState);
    }

}
