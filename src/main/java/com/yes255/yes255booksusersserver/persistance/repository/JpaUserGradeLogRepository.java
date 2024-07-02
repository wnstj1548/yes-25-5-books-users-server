package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.UserGradeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserGradeLogRepository extends JpaRepository<UserGradeLog, Long> {

    Optional<UserGradeLog> findFirstByUserUserIdOrderByUserGradeUpdatedAtDesc(Long userId);
}
