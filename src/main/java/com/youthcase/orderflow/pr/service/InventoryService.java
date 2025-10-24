package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.domain.Inventory;
import com.youthcase.orderflow.pr.repository.InventoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @PersistenceContext
    private EntityManager em;

    // ───────── 조회 ─────────

    @Transactional(readOnly = true)
    public Long getAvailable(String gtin) {
        return inventoryRepository.findByProduct_Gtin(gtin)
                .map(Inventory::getAvailable)
                .orElse(0L);
    }

    @Transactional(readOnly = true)
    public Inventory getOrNull(String gtin) {
        return inventoryRepository.findByProduct_Gtin(gtin).orElse(null);
    }

    // ───────── 생성/업서트 ─────────

    /** 존재하지 않으면 생성(온핸드는 0으로 시작) */
    @Transactional
    public Inventory getOrCreate(String gtin) {
        var inv = inventoryRepository.findByProduct_Gtin(gtin).orElse(null);
        if (inv != null) return inv;

        // master 모듈의 Product 프록시 참조 (존재해야 함)
        var productRef = em.getReference(com.youthcase.orderflow.master.product.domain.Product.class, gtin);

        inv = new Inventory();
        inv.setProduct(productRef);
        inv.setOnHand(0L);
        inv.setReserved(0L);
        return inventoryRepository.save(inv);
    }

    // ───────── 비즈니스 동작 ─────────

    /** 재고 예약 (장바구니/주문hold) */
    @Transactional
    public void reserve(String gtin, int qty) {
        if (qty <= 0) return;
        var inv = getOrCreate(gtin);
        if (inv.getAvailable() < qty) {
            throw new NotEnoughInventoryException("가용 재고 부족: " + gtin);
        }
        inv.setReserved(inv.getReserved() + qty);
        // JPA flush는 트랜잭션 종료 시점에
    }

    /** 예약 해제 (장바구니 취소 등) */
    @Transactional
    public void release(String gtin, int qty) {
        if (qty <= 0) return;
        var inv = inventoryRepository.findByProduct_Gtin(gtin)
                .orElseThrow(() -> new IllegalArgumentException("재고가 없습니다: " + gtin));
        Long newReserved = Math.max(0L, inv.getReserved() - qty);
        inv.setReserved(newReserved);
    }

    /** 출고/판매 확정: 예약 → 실제 소진 */
    @Transactional
    public void commit(String gtin, int qty) {
        if (qty <= 0) return;
        var inv = inventoryRepository.findByProduct_Gtin(gtin)
                .orElseThrow(() -> new IllegalArgumentException("재고가 없습니다: " + gtin));
        if (inv.getReserved() < qty) {
            throw new IllegalStateException("예약 수량 부족: " + gtin);
        }
        if (inv.getOnHand() < qty) {
            // 이 상황은 동시성 등으로 가끔 생길 수 있음
            throw new NotEnoughInventoryException("실 재고 부족: " + gtin);
        }
        inv.setReserved(inv.getReserved() - qty);
        inv.setOnHand(inv.getOnHand() - qty);
    }

    /** 입고(수량 증가) */
    @Transactional
    public void receive(String gtin, int qty) {
        if (qty <= 0) return;
        var inv = getOrCreate(gtin);
        inv.setOnHand(inv.getOnHand() + qty);
    }

    /** 재고 수량 강제 설정(관리자용) */
    @Transactional
    public void setOnHand(String gtin, Long onHand) {
        if (onHand < 0L) onHand = 0L;
        var inv = getOrCreate(gtin);
        inv.setOnHand(onHand);
        // reserved가 onHand보다 클 수 있으니, 비즈니스에 맞게 추가처리할지 결정
    }

    // ───────── 선택: 낙관적 락 재시도 래퍼 ─────────

    @Transactional
    public void withRetry(Runnable work) {
        int tries = 0;
        while (true) {
            try {
                work.run();
                return;
            } catch (OptimisticLockException ole) {
                if (++tries >= 3) throw ole;
                // 짧게 재시도
            }
        }
    }
}
