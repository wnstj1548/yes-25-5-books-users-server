package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.UserGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaUserGradeRepository extends JpaRepository<UserGrade, Long> {

//    List<UserGrade> findByUser_UserId(Long userId);

    UserGrade findByUserGradeName(String userGradeName);

//    List<UserGrade> findByUser_UserIdAndPointPolicy_PointPolicyNameIn(Long userId, List<String> pointPolicyNames);

    UserGrade findByPointPolicy_PointPolicyId(Long pointPolicyId);

//    List<UserGrade> findAllByPointPolicy_PointPolicyApplyAmount(Long pointPolicyId);
}
