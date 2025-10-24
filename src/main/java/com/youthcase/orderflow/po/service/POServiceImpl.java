package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.UserRepository;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class POServiceImpl implements POService {

    private final POHeaderRepository poHeaderRepository;
    private final POItemRepository poItemRepository;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final UserRepository userRepository;

    // ---------------------- 공통 메서드 ----------------------

    // 총 합계 수량 메서드
    private Long calculateTotalAmountForHeader(Long poId){
        List<POItem> items = poItemRepository.findByPoHeader_PoId(poId);
        return items.stream()
                .mapToLong(item -> item.getPrice().getPurchasePrice() * item.getOrderQty())
                .sum();
    }

    /** POHeader 생성 */
    public POHeaderResponseDTO createNewPOHeader() {
        // 바코드 번호 생성
        LocalDate today = LocalDate.now();
        String branchCode = "S003"; // TODO: 로그인 지점 코드로 변경
        long countToday = poHeaderRepository.countByActionDateAndBranchCode(today, branchCode);
        String seq = String.format("%02d", countToday + 1);
        String datePart = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        String externalId = datePart + branchCode + seq; // 예: 20251025S00301

        POHeader header = new POHeader();
        header.setStatus(POStatus.PR);
        header.setActionDate(today);
        header.setExternalId(externalId);

        poHeaderRepository.save(header);

        Long totalAmount = calculateTotalAmountForHeader(header.getPoId());
        header.setTotalAmount(totalAmount);

        poHeaderRepository.save(header);

        return POHeaderResponseDTO.builder()
                .poId(header.getPoId())
                .status(header.getStatus())
                .totalAmount(header.getTotalAmount())
                .actionDate(header.getActionDate())
                .remarks(header.getRemarks())
                .externalId(header.getExternalId())
                .build();
    }

    // ---------------------- 서비스 구현 ----------------------

    /** 새 헤더 생성 + 아이템 추가 */
    @Override
    public POHeaderResponseDTO createHeaderAndItem(String gtin, POItemRequestDTO dto) {

        return createNewPOHeader();
    }


    /** 기존 헤더에 아이템 추가 */
    @Override
    public POItemResponseDTO addPOItem(Long poId, POItemRequestDTO dto, String gtin) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("POHeader not found: " + poId));

        Product product = productRepository.findByGtin(gtin)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Price price = priceRepository.findByGtin(gtin)
                .orElseThrow(() -> new IllegalArgumentException("Price not found"));

        Optional<POItem> existingItemOpt = poItemRepository.findByPoHeaderAndGtin(header, product);

        POItem poItem;
        if (existingItemOpt.isPresent()) {
            poItem = existingItemOpt.get();
            Long newQty = poItem.getOrderQty() + dto.getOrderQty();
            poItem.setOrderQty(newQty);
            poItem.setTotal(price.getPurchasePrice() * newQty);
        } else {
            Long total = price.getPurchasePrice() * dto.getOrderQty();
            poItem = POItem.builder()
                    .itemNo(dto.getItemNo())
                    .poHeader(header)
                    .gtin(product)
                    .price(price)
                    .orderQty(dto.getOrderQty())
                    .total(total)
                    .expectedArrival(LocalDate.now().plusDays(3))
                    .status(POStatus.PR)
                    .build();
        }

        poItemRepository.save(poItem);
        return toResponseDTO(poItem);
    }

    /** 모든 헤더 조회 */
    @Override
    public List<POHeaderResponseDTO> findAll() {
        return poHeaderRepository.findAll().stream()
                .map(POHeaderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 장바구니 상품 조회 */
    @Override
    public List<POItemResponseDTO> getAllItems(Long poId) {
        return poItemRepository.findByPoHeader_PoId(poId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** 상품 수량 변경 */
    @Override
    public POItemResponseDTO updateItemQuantity(Long itemNo, POItemRequestDTO requestDTO) {
        POItem item = poItemRepository.findByItemNoAndStatus(itemNo, POStatus.PR)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다."));
        if (requestDTO.getOrderQty() < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        item.setOrderQty(requestDTO.getOrderQty());
        poItemRepository.save(item);
        return toResponseDTO(item);
    }

    /** 상품 삭제 */
    @Override
    public void deleteItem(List<Long> itemNos) {
        poItemRepository.deleteAllById(itemNos);
    }

    /** 장바구니 저장 */
    @Override
    public void saveCart(Long poId, String remarks) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주 헤더가 존재하지 않습니다."));
        header.setStatus(POStatus.S);
        header.setRemarks(remarks);
        poHeaderRepository.save(header);
    }

    /** 저장된 장바구니 목록 */
    @Override
    public List<POHeaderResponseDTO> getSavedCartList() {
        return poHeaderRepository.findByStatus(POStatus.S).stream()
                .map(POHeaderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 특정 장바구니 불러오기 */
    @Override
    public List<POItemResponseDTO> getSavedCartItems(Long poId) {
        return poItemRepository.findByPoHeader_PoId(poId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** 저장된 장바구니 삭제 */
    @Override
    public void deletePO(Long poId) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("POHeader not found: " + poId));
        poHeaderRepository.delete(header);
    }

    /** 발주 확정 */
    @Override
    public void confirmOrder(Long poId) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주가 존재하지 않습니다."));
        header.setStatus(POStatus.PO); // 확정 상태로 변경
        poHeaderRepository.save(header);
    }

    /** 입고 진행률 업데이트 */
    @Override
    public void updateReceiveProgress(Long poId) {
        // TODO: 실제 입고 처리 로직 추가
        System.out.println("입고 진행률 갱신: " + poId);
    }

    // ---------------------- 변환 헬퍼 ----------------------

    private POItemResponseDTO toResponseDTO(POItem item) {
        return POItemResponseDTO.builder()
                .itemNo(item.getItemNo())
                .gtin(item.getGtin().getGtin())
                .productName(item.getGtin().getProductName())
                .purchasePrice(item.getPrice().getPurchasePrice())
                .orderQty(item.getOrderQty())
                .total(item.getTotal())
                .status(item.getStatus())
                .build();
    }
}