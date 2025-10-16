package com.youthcase.orderflow.master.repository;

import com.youthcase.orderflow.master.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {

    boolean existsByStoreId(String storeId); // 중복 등록 방지용
}
