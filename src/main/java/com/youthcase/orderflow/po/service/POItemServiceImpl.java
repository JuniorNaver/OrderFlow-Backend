package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.Status;
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

    private static final Status statusPr = "PR";   // 장바구니 상태
    private static final String statusD = "D"; // 삭제 상태
    private static final String statusGI = "GI"; // 출고 처리 상태

    // 장바구니 전체 상품 조회 (status = PR)
    @Override
    public List<POItem> getAllItems() {
        return poItemRepository.findAllByStatus(Status.PR);
    }

    // 상품 수량 변경
    @Override
    public POItem updateItemQuantity(Long gtin, int quantity) {
        POItem item = poItemRepository.findByGtinAndStatus(gtin, statusPr)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. GTIN: " + gtin));

        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        item.setOrderQty((long) quantity);
        return poItemRepository.save(item);
    }

    // 선택 상품 삭제 (status = D 로 변경)
    @Override
    public void deleteItem(List<Long> gtins) {
        for (Long gtin : gtins) {
            POItem item = poItemRepository.findByGtinAndStatus(gtin, statusPr)   //pr 일때
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. GTIN: " + gtin));
            item.setStatus(Status.D);
            poItemRepository.save(item);
        }
    }

    // 전체 상품 삭제 (장바구니 status = PR → D 로 일괄 변경)
    @Override
    public void clearCart() {
        List<POItem> cartItems = poItemRepository.findAllByStatus(Status.PR);
        for (POItem item : cartItems) {
            item.setStatus(Status.D);
        }
        poItemRepository.saveAll(cartItems);
    }


}
