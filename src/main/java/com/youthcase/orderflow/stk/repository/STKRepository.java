package com.youthcase.orderflow.stk.repository;

import com.youthcase.orderflow.stk.domain.STK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface STKRepository extends JpaRepository<STK, Long> {
    // TODO: 필요 시 커스텀 쿼리 추가
}
