package com.youthcase.orderflow.master.repository;

import com.youthcase.orderflow.master.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    // ✅ Store FK 기준으로 조회
    List<Warehouse> findByStore_StoreId(String storeId);
}