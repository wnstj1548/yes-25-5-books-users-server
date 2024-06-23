package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPointRepository extends JpaRepository<Point, Long> {

    Point findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
