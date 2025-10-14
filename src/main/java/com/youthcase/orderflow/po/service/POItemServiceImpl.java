package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
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

    private final POHeaderRepository poHeaderRepository;
    private final POItemRepository poItemRepository;

    /** 장바구니 */
    @Override
    public List<POItemResponseDTO> getAllItems(Long poId, POStatus status) {
        return poItemRepository.findByPoHeader_PoIdAndStatus(poId, status)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /** 수량변경 */
    @Override
    public POItemResponseDTO updateItemQuantity(Long itemNo, POItemRequestDTO requestDTO) {
        Long quantity = requestDTO.getOrderQty();
        // 2️⃣ 기존 아이템 조회 (Status.PR 상태 기준)
        POItem item = poItemRepository.findByItemNoAndStatus(itemNo, POStatus.PR)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다."));
        // 3️⃣ 유효성 검사
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        // 4️⃣ 수량 변경
        item.setOrderQty(quantity);
        // 5️⃣ 저장 및 엔티티 → DTO 변환
        POItem updated = poItemRepository.save(item);
        return toResponseDTO(updated);
    }
    /** Entity → DTO 변환 */
    private POItemResponseDTO toResponseDTO(POItem item) {
        return POItemResponseDTO.builder()
                .itemNo(item.getItemNo())
                .productName(item.getGtin().getProductName()) // 상품명을 가져오는 방법. gtin = Product { gtin = "8801234567890", productName = "햇반 100g" }
                .gtin(item.getGtin().getGtin())   // ✅ 필드명 DTO 기준으로 통일
                .expectedArrival(item.getExpectedArrival())
                .unitPrice(item.getUnitPrice())
                .orderQty(item.getOrderQty())
                .total(item.getTotal())
                .status(item.getStatus())
                .build();
    }

    /** 상품삭제 */
    @Override
    @Transactional
    public void deleteItem(List<Long> itemNos) {
        poItemRepository.deleteAllById(itemNos);
    }

    /** 장바구니 저장 */
    @Override
    @Transactional
    public void updateStatusToSaved(Long poId) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주 헤더가 존재하지 않습니다."));

        header.setStatus(POStatus.S);
        poHeaderRepository.save(header);
    }

    /** 저장된 장바구니 목록 불러오기 (status = S 인 헤더 전체) */
    @Override
    @Transactional(readOnly = true)
    public List<POHeaderResponseDTO> getSavedCartList(Long poId) {
        // 1️⃣ Status 가 S 인 헤더 조회
        List<POHeader> savedHeaders = poHeaderRepository.findByStatus(POStatus.S);

        // 2️⃣ DTO 변환
        return savedHeaders.stream()
                .map(header -> POHeaderResponseDTO.builder()
                        .poId(header.getPoId())
                        .status(header.getStatus())
                        .build())
                .toList();
    }

    /** 특정 장바구니 아이템 불러오기 */
    @Override
    @Transactional(readOnly = true)
    public List<POItemResponseDTO> getSavedCartItems(Long poId) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주 헤더가 존재하지 않습니다. ID=" + poId));

        List<POItem> items = poItemRepository.findByPoHeader_PoId(poId);

        return items.stream()
                .map(item -> POItemResponseDTO.builder()
                        .itemNo(item.getItemNo())
                        .unitPrice(item.getUnitPrice())
                        .orderQty(item.getOrderQty())
                        .gtin(item.getGtin().getGtin())
                        .build())
                .toList();
    }
}

