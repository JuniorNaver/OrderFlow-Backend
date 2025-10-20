package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.master.price.repository.PriceRepository;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import com.youthcase.orderflow.po.repository.POItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class POItemServiceImpl implements POItemService {

    private final POHeaderRepository poHeaderRepository;
    private final POItemRepository poItemRepository;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;

    /** '담기' 클릭시 POItem에 데이터 생성 */
    @Override
    public POItemResponseDTO addPOItem(Long poId, POItemRequestDTO dto, String gtin) {

        // 1️⃣ 헤더 찾기
        POHeader poHeader = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("POHeader not found: " + poId));

        // 2️⃣ GTIN 으로 상품 조회
        Product product = productRepository.findByGtin(gtin)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 3️⃣ 가격 조회 (Product에 매핑된 Price)
        Price price = priceRepository.findByProduct_Gtin(gtin)
                .orElseThrow(() -> new IllegalArgumentException("Price not found"));

        // 4️⃣ 총액 계산
        Long total = price.getPurchasePrice() * dto.getOrderQty();

        // 5️⃣ POItem 엔티티 생성
        POItem poItem = POItem.builder()
                .itemNo(dto.getItemNo())
                .poHeader(poHeader)
                .gtin(product)
                .price(price) // ✅ Price 엔티티 전체 주입
                .orderQty(dto.getOrderQty())
                .total(total)
                .expectedArrival(LocalDate.now().plusDays(3))
                .status(POStatus.PR)
                .build();

        // 6️⃣ 저장
        POItem saved = poItemRepository.save(poItem);

        // 7️⃣ 응답 DTO 반환
        return POItemResponseDTO.builder()
                //.itemNo(saved.getItemNo())
                .gtin(saved.getGtin().getGtin())
                .productName(saved.getGtin().getProductName())
                .orderQty(saved.getOrderQty())
                .purchasePrice(saved.getPrice()) // ✅ 단가 접근
                .total(saved.getTotal())
                .status(saved.getStatus())
                .build();
    }




    /** 장바구니 호출　*/
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
                .purchasePrice(item.getPrice())
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
                        .purchasePrice(item.getPrice())
                        .orderQty(item.getOrderQty())
                        .gtin(item.getGtin().getGtin())
                        .build())
                .toList();
    }
}

