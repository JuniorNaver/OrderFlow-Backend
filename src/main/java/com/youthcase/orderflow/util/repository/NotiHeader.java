package com.youthcase.orderflow.util.repository;

import com.youthcase.orderflow.util.domain.NotiHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<[엔티티 타입], [PK 타입]>
@Repository
public interface NotiHeaderRepository extends JpaRepository<NotiHeader, Long> {

    // 알림 유형(TYPE)이나 대상 ID(STK_ID)로 헤더를 찾는 메서드를 추가할 수 있습니다.
    // List<NotiHeader> findByType(String type);
    // Optional<NotiHeader> findByStkId(Long stkId);
}