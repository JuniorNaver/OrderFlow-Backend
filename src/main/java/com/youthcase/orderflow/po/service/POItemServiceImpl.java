package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.repository.POItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class POItemServiceImpl implements POItemService {

    private final POItemRepository poItemRepository;

    /**
     * 장바구니 전체 상품 조회
     */
    @Override
    public List<POItem> getAllItems() {
        return poItemRepository.findAll();
    }

    /**
     * 상품 수량 변경
     */
    @Override
    public POItem updateItemQuantity(String gtin, int orderQty) {
        POItem item = poItemRepository.findByProductGtin(gtin)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. GTIN: " + gtin));

        // 최소 수량 체크
        if (orderQty < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        item.setOrderQty((long) orderQty);
        return poItemRepository.save(item);
    }

    /**
     * 선택 상품 삭제
     */
    @Override
    public void deleteItem(List<String> gtins) {
        for (String gtin : gtins) {
            poItemRepository.deleteByProductGtin(gtin);
        }
    }

    /**
     * 전체 상품 삭제
     */
    @Override
    public void clearCart() {
        poItemRepository.deleteAll();
    }
}
