package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.gr.domain.GoodsReceiptItem;
import com.youthcase.orderflow.gr.repository.GoodsReceiptItemRepository;
import com.youthcase.orderflow.po.domain.PO;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import com.youthcase.orderflow.po.repository.POItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class POServiceImpl implements POService {

    private final POHeaderRepository poHeaderRepository;
    private final POItemRepository poItemRepository;
    private final GoodsReceiptItemRepository grItemRepo;

    @Override
    public PO confirmOrder(Long poId) {
        // 1. 발주 헤더 조회
        POHeader poHeader = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다."));

        // 2. 상태를 'PO'로 변경
        poHeader.setStatus(POStatus.PO);

        // 3. 저장
        POHeader savedHeader = poHeaderRepository.save(poHeader);

        // 4. 관련된 아이템 조회
        List<POItem> items = poItemRepository.findByPoHeader_PoId(poId);

        // 5. 본사로 전송 (추후 실제 구현)
        sendToHQ(savedHeader, items);

        // 6. DTO 로 묶어서 리턴
        return new PO(savedHeader, items);
    }

    // 본사로 발주 정보 전송
    private void sendToHQ(POHeader poHeader, List<POItem> items) {
        System.out.println("본사로 발주 전송: HeaderID=" + poHeader.getPoId());
    }

    @Override
    public void updateReceiveProgress(Long poId) {

        // 1️⃣ 발주 헤더 조회
        POHeader poHeader = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("발주를 찾을 수 없습니다."));

        // 2️⃣ 발주 아이템 조회
        List<POItem> poItems = poItemRepository.findByPoHeader_PoId(poId);
        if (poItems.isEmpty()) {
            log.warn("해당 발주에 품목이 없습니다. poId={}", poId);
            return;
        }

        // 3️⃣ 입고된 아이템 조회
        List<GoodsReceiptItem> grItems = grItemRepo.findByHeader_PoHeader_PoId(poId);
        long totalOrdered = poItems.stream().mapToLong(POItem::getOrderQty).sum();
        long totalReceived = grItems.stream().mapToLong(GoodsReceiptItem::getQty).sum();

        // 4️⃣ 상태 결정
        POStatus newStatus;
        if (totalReceived == 0) {
            newStatus = POStatus.PO; // 아직 입고 없음 → 발주 상태 유지
        } else if (totalReceived < totalOrdered) {
            // 일부 입고 → PARTIAL_RECEIVED 상태 필요
            try {
                newStatus = POStatus.valueOf("PARTIAL_RECEIVED");
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Enum에 PARTIAL_RECEIVED 없음. 상태를 PO로 유지함.");
                newStatus = POStatus.PO;
            }
        } else {
            // 전량 입고 → FULLY_RECEIVED 상태 필요
            try {
                newStatus = POStatus.valueOf("FULLY_RECEIVED");
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Enum에 FULLY_RECEIVED 없음. 상태를 GI(출고 대기)로 변경함.");
                newStatus = POStatus.GI;
            }
        }

        // 5️⃣ 상태 업데이트 및 저장
        poHeader.setStatus(newStatus);
        poHeaderRepository.save(poHeader);

        log.info("📦 발주 입고 진행도 갱신: poId={}, 주문수량={}, 입고수량={}, 상태={}",
                poId, totalOrdered, totalReceived, newStatus);
    }

}



