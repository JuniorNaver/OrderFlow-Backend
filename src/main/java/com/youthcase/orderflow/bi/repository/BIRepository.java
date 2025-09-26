package com.youthcase.orderflow.bi.repository;

import com.youthcase.orderflow.bi.domain.BI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BIRepository extends JpaRepository<BI, Long> {
    // TODO: 필요 시 커스텀 쿼리 추가
}
