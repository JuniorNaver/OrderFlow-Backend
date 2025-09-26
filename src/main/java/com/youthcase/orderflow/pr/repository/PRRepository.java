package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.PR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PRRepository extends JpaRepository<PR, Long> {
    // TODO: 필요 시 커스텀 쿼리 추가
}
