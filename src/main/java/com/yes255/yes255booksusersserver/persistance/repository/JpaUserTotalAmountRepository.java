package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserTotalAmount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserTotalAmountRepository extends JpaRepository<UserTotalAmount, Long> {

    UserTotalAmount findByUser_UserId(Long userId);

    void deleteByUser(User user);
}
