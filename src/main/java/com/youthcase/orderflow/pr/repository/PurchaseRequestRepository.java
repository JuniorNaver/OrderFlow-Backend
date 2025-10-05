package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.PurchaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    Page<PurchaseRequest> findByStoreId(String storeId, Pageable pageable);
}
