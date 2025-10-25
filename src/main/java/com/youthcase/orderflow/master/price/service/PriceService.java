package com.youthcase.orderflow.master.price.service;

import com.youthcase.orderflow.master.price.dto.PriceRequestDTO;
import com.youthcase.orderflow.master.price.dto.PriceResponseDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 💼 PriceService
 * - GTIN(=ID) 기준 가격 관리 및 조회 서비스 인터페이스
 * - 관리자 CRUD + 도메인별 조회(매입/매출/통합)
 */
public interface PriceService {

    /**
     * [C] 신규 등록
     * - Product 존재 여부를 확인한 뒤 가격 정보를 생성
     * - GTIN 중복 시 예외 발생
     */
    PriceResponseDTO createPrice(PriceRequestDTO request);

    /**
     * [U] 수정
     * - GTIN 기준으로 매입/매출 단가를 업데이트
     * - 존재하지 않으면 예외 발생
     */
    PriceResponseDTO updatePrice(PriceRequestDTO request);

    /**
     * [D] 삭제
     * - GTIN 기준으로 가격 정보를 삭제
     * - 존재하지 않으면 예외 발생
     */
    void deletePrice(String gtin);

    /**
     * [R] 단일 매입+매출 단가 조회 (매입 + 매출 단가)
     * - GTIN 기준으로 Price 엔티티 조회 후 DTO 변환
     */
    PriceResponseDTO getPrice(String gtin);

    /**
     * [R] 단일 매입가 조회 (PR/PO 모듈용)
     * - GTIN 기준으로 매입 단가(BigDecimal)만 반환
     */
    BigDecimal getPurchasePrice(String gtin);

    /**
     * [R] 단일 매출가 조회 (SD 모듈용)
     * - GTIN 기준으로 매출 단가(BigDecimal)만 반환
     */
    BigDecimal getSalePrice(String gtin);

    /** [R] 전체 조회 (관리자용 리스트업) */
    List<PriceResponseDTO> getAllPrices();
}
