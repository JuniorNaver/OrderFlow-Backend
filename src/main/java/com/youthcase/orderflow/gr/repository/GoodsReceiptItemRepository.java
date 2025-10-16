package com.youthcase.orderflow.gr.repository;

import com.youthcase.orderflow.gr.domain.GoodsReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsReceiptItemRepository extends JpaRepository<GoodsReceiptItem, Long> {

    /**
     * 입고헤더 ID 기준으로 하위 아이템 조회
     */
    List<GoodsReceiptItem> findByHeader_Id(Long grHeaderId);

    /**
     * 특정 GTIN 기준으로 입고아이템 전체 조회
     */
    List<GoodsReceiptItem> findByProduct_Gtin(String gtin);
}
