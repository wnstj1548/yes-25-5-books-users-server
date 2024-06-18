package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    List<User> findAllByUserNameAndUserPhone(String userName, String userPhone, Pageable pageable);

    User findByUserEmailAndUserPassword(String username, String password);

    User findByUserEmail(String userEmail);

    User findByUserEmailAndUserName(String userEmail, String userName);
}
