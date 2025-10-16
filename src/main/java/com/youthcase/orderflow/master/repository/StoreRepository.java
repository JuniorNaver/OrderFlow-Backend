package com.youthcase.orderflow.master.repository;

import com.youthcase.orderflow.master.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {

    // 필요 시 맞춤형 조회도 추가 가능
    Store findByStoreName(String storeName);

    boolean existsByStoreId(String storeId);
}
