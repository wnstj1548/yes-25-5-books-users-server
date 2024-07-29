package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Collectors;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    List<User> findAllByUserNameAndUserPhone(String userName, String userPhone);

    User findByUserEmailAndUserPassword(String username, String password);

    User findByUserEmail(String userEmail);

    User findByUserEmailAndUserName(String userEmail, String userName);

    List<User> findAllByUserState(UserState state);

    Boolean existsByUserEmail(String userEmail);

    default List<User> findUsersByBirthMonth(int month) {
        return findAll().stream()
                .filter(user -> user.getUserBirth().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    @Query("SELECT u FROM User u WHERE FUNCTION('MONTH', u.userBirth) = :month AND FUNCTION('DAY', u.userBirth) = :day")
    List<User> findUsersByBirthMonthAndDay(@Param("month") int month, @Param("day") int day);

    List<User> findByUserIdIn(List<Long> userIds);
}