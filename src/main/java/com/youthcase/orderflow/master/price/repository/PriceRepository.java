package com.youthcase.orderflow.master.price.repository;

import com.youthcase.orderflow.master.price.domain.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 📘 PriceRepository
 * - 가격 마스터(매입/매출 단가) 기본 JPA 레포지토리
 * - GTIN(=ID) 기준 조회 중심
 */
public interface PriceRepository extends JpaRepository<Price, String> {

    /** 🔹 GTIN 기준 매입가 조회 */
    @Query("SELECT p.purchasePrice FROM Price p WHERE p.gtin = :gtin")
    Optional<BigDecimal> findPurchasePriceByGtin(@Param("gtin") String gtin);

    /** 🔹 GTIN 기준 매출가 조회 */
    @Query("SELECT p.salePrice FROM Price p WHERE p.gtin = :gtin")
    Optional<BigDecimal> findSalePriceByGtin(@Param("gtin") String gtin);

}