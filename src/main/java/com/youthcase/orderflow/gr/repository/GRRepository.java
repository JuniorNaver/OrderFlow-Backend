package com.youthcase.orderflow.gr.repository;

import com.youthcase.orderflow.gr.domain.GR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GRRepository extends JpaRepository<GR, Long> {
    // TODO: 필요 시 커스텀 쿼리 추가
}
