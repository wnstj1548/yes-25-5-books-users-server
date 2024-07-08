package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserTotalPureAmount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserTotalPureAmountRepository extends JpaRepository<UserTotalPureAmount, Long> {

    Optional<UserTotalPureAmount> findByUserUserId(Long userId);

    Optional<UserTotalPureAmount> findFirstByUserUserIdOrderByUserTotalPureAmountRecordedAtDesc(Long userId);

    void deleteByUser(User user);
}
