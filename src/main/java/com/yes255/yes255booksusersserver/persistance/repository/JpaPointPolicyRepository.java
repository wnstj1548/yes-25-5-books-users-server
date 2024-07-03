package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.PointPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPointPolicyRepository extends JpaRepository<PointPolicy, Long> {

    PointPolicy findByPointPolicyName(String pointPolicyName);
    
    Page<PointPolicy> findAllBy(Pageable pageable);

    Page<PointPolicy> findAllByOrderByPointPolicyCreatedAtAscPointPolicyStateDesc(Pageable pageable);
}
