package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.PointLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaPointLogRepository extends JpaRepository<PointLog, Long> {

    List<PointLog> findByPoint_User_UserId(Long userId, Pageable pageable);

}
