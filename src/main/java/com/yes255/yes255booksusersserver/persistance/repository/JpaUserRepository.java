package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Collectors;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    List<User> findAllByUserNameAndUserPhone(String userName, String userPhone, Pageable pageable);

    User findByUserEmailAndUserPassword(String username, String password);

    User findByUserEmail(String userEmail);

    User findByUserEmailAndUserName(String userEmail, String userName);

    List<User> findAllByUserState(UserState state);

    Boolean existsByUserEmail(String userEmail);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.customer LEFT JOIN FETCH u.provider " +
            "LEFT JOIN FETCH u.userState LEFT JOIN FETCH u.userGrade " +
            "WHERE MONTH(u.userBirth) = :month")
    List<User> findUsersByBirthMonth(@Param("month") int month);
}