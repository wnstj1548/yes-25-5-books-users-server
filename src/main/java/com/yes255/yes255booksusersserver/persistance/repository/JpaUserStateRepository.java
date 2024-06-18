package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserStateRepository extends JpaRepository<UserState, Long> {

    UserState findByUserStateName(String UserStateName);
}
