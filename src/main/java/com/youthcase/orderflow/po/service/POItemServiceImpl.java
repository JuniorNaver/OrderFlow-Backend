package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.Status;
import com.youthcase.orderflow.po.repository.POItemRepository;
import com.youthcase.orderflow.pr.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class POItemServiceImpl implements POItemService {

    private final POItemRepository poItemRepository;
    private static final Status statusPr = Status.PR;   // 장바구니 상태
    private static final Status statusD = Status.D;     // 삭제 상태
    private static final Status statusGI = Status.GI;        // 출고 처리 상태

    // 장바구니 상품 조회
    @Override
    public List<POItem> getAllItems(Long poId) {
        return poItemRepository.findByPoHeader_PoIdAndStatus(poId, Status.PR);
    }

    // 상품 수량 변경
    @Override
    public POItem updateItemQuantity(Long gtin, int quantity) {
        POItem item = poItemRepository.findByGtinAndStatus(gtin, statusPr).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. GTIN: " + gtin));

        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        item.setOrderQty((long) quantity);
        return poItemRepository.save(item);   // save() : 쿼리에서 update 로 작동한다.
    }

    // 선택 상품 삭제
    @Override
    @Transactional
    public void deleteItem(List<Long> gtins) {
        List<POItem> items = poItemRepository.findAllByGtinIn(gtins);
        poItemRepository.deleteAll(items);
    }




//    장바구니 전체를 삭제할 때, 전체 삭제 버튼이 있나? 음.. 아니야
//    전체 선택 했을 때만 논리적 삭제를 한다? 뭔가 이상한데..
//
//    저장 버튼을 아예 따로 만들까? 저장 버튼..  그게 훨씬 현실적이긴 하지..

    // 전체 상품 삭제 (status = PR → D 로 변경)
    @Override
    public void clearItem() {
        List<POItem> cartItems = poItemRepository.findAllByStatus(Status.PR);
        for (POItem item : cartItems) {
            item.setStatus(Status.D);
        }
        poItemRepository.saveAll(cartItems);
    }

}
