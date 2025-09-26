package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.PO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PORepository extends JpaRepository<PO, Long> {
    // TODO: 필요 시 커스텀 쿼리 추가
}
