package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.POHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface POHeaderRepository extends JpaRepository<POHeader, Long> {
    // 기본적인 save(), findById(), delete() 같은 CRUD 메서드는 JpaRepository가 제공하므로 추가할 필요 없음

}