// src/backend/src/main/java/com/orderflow/receipt/service/LotService.java
package com.youthcase.orderflow.gr.service;

import com.orderflow.common.exception.ResourceNotFoundException;
import com.orderflow.receipt.dto.LotDto;
import com.orderflow.receipt.entity.Lot;
import com.orderflow.receipt.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LOT 관리 서비스
 * MM_GR_004: LOT 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LotService {
    
    private final LotRepository lotRepository;
    
    /**
     * LOT 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<LotDto> getLots(Pageable pageable) {
        return lotRepository.findAll(pageable)
                .map(LotDto::from);
    }
    
    /**
     * LOT 목록 조회 (상품별)
     */
    @Transactional(readOnly = true)
    public List<LotDto> getLotsByProduct(String productCode) {
        return lotRepository.findByProductProductCodeAndStatusOrderByExpiryDateAsc(
                productCode, 
                Lot.LotStatus.AVAILABLE
        ).stream()
                .map(LotDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * LOT 목록 조회 (유통기한 임박)
     * MM_GR_005: 유통기한 알림
     */
    @Transactional(readOnly = true)
    public List<LotDto> getNearExpiryLots(int days) {
        LocalDate today = LocalDate.now();
        LocalDate alertDate = today.plusDays(days);
        
        return lotRepository.findByExpiryDateBetweenAndStatusOrderByExpiryDateAsc(
                today, 
                alertDate, 
                Lot.LotStatus.AVAILABLE
        ).stream()
                .map(LotDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * LOT 목록 조회 (만료된 것)
     */
    @Transactional(readOnly = true)
    public List<LotDto> getExpiredLots() {
        return lotRepository.findByExpiryDateBeforeAndStatusNot(
                LocalDate.now(), 
                Lot.LotStatus.DISPOSED
        ).stream()
                .map(LotDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * LOT 상세 조회
     */
    @Transactional(readOnly = true)
    public LotDto getLot(Long lotId) {
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Lot", "id", lotId));
        return LotDto.from(lot);
    }
    
    /**
     * LOT 번호로 조회
     */
    @Transactional(readOnly = true)
    public LotDto getLotByNumber(String lotNumber) {
        Lot lot = lotRepository.findByLotNumber(lotNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Lot", "lotNumber", lotNumber));
        return LotDto.from(lot);
    }
    
    /**
     * LOT 상태 일괄 업데이트
     * 배치 작업용 - 유통기한 기준으로 상태 업데이트
     */
    @Transactional
    public int updateLotStatuses() {
        List<Lot> lots = lotRepository.findByStatusNot(Lot.LotStatus.DISPOSED);
        int updatedCount = 0;
        
        for (Lot lot : lots) {
            Lot.LotStatus oldStatus = lot.getStatus();
            
            if (lot.isExpired()) {
                lot.setStatus(Lot.LotStatus.EXPIRED);
            } else if (lot.isNearExpiry(7)) {
                lot.setStatus(Lot.LotStatus.NEAR_EXPIRY);
            } else {
                lot.setStatus(Lot.LotStatus.AVAILABLE);
            }
            
            if (oldStatus != lot.getStatus()) {
                lotRepository.save(lot);
                updatedCount++;
                log.debug("LOT status updated: {} ({} -> {})", 
                        lot.getLotNumber(), oldStatus, lot.getStatus());
            }
        }
        
        if (updatedCount > 0) {
            log.info("Updated {} lot statuses", updatedCount);
        }
        
        return updatedCount;
    }
    
    /**
     * LOT 폐기 처리
     */
    @Transactional
    public void disposeLot(Long lotId) {
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Lot", "id", lotId));
        
        lot.setStatus(Lot.LotStatus.DISPOSED);
        lotRepository.save(lot);
        
        log.info("LOT disposed: {}", lot.getLotNumber());
    }
}
