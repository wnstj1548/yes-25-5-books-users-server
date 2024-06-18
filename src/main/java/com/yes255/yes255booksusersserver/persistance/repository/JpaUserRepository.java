package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    List<User> findAllByUserNameAndUserPhone(String userName, String userPhone);

    User findByUserIdAndUserEmail(Long userId, String username);

    User findByUserEmail(String userEmail);
}
