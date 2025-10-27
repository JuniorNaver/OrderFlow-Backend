package com.youthcase.orderflow.master.warehouse.repository;

import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import com.youthcase.orderflow.master.store.domain.Store;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    // ✅ Store FK 기준으로 조회
    List<Warehouse> findByStore_StoreId(String storeId);

    // ✅ 점포(Store) ID 기준으로 첫 번째 창고 1개만 반환 (Optional)
    Optional<Warehouse> findFirstByStore_StoreId(String storeId);

    Optional<Warehouse> findByStore(Store store);

    List<Warehouse> findByStorageMethod(StorageMethod storageMethod);
}



