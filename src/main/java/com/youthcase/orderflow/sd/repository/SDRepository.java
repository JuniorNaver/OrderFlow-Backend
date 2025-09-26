package com.youthcase.orderflow.sd.repository;

import com.youthcase.orderflow.sd.domain.SD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SDRepository extends JpaRepository<SD, Long> {
    // TODO: 필요 시 커스텀 쿼리 추가
}
