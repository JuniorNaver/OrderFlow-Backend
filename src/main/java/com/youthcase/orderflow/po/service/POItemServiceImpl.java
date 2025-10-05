package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.Status;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import com.youthcase.orderflow.po.repository.POItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class POItemServiceImpl implements POItemService {

    private final POHeaderRepository poHeaderRepository;
    private final POItemRepository poItemRepository;

    // 장바구니 상품 조회
    @Override
    public List<POItem> getAllItems(Long poId, Status status) {
        return poItemRepository.findByPoHeader_PoIdAndStatus(poId, Status.PR);
    }

    // 상품 수량 변경
    @Override
    public POItem updateItemQuantity(Long itemNo, Long quantity) {
        POItem item = poItemRepository.findByItemNoAndStatus(itemNo, Status.PR)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다."));

        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        item.setOrderQty(quantity);
        return poItemRepository.save(item);   // save() : 쿼리에서 update 로 작동한다.
    }

    // 선택 상품 삭제
    @Override
    @Transactional
    public void deleteItem(List<Long> itemNos) {
        poItemRepository.deleteAllById(itemNos);
    }

    // 장바구니 저장 (status = PR → S로 변경)
    @Override
    public void saveItem(Long poId) {
        POHeader poHeader = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주서가 존재하지 않습니다."));

        // 헤더 상태만 변경
        poHeader.setStatus(Status.S);

        // JPA가 자동으로 변경 감지하여 업데이트
        poHeaderRepository.save(poHeader);
    }

}
