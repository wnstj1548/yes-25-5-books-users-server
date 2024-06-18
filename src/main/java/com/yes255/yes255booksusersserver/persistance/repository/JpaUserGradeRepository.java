package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.UserGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserGradeRepository extends JpaRepository<UserGrade, Long> {

    UserGrade findByUserGradeName(String userGradeName);
}
