package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.master.price.repository.PriceRepository;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.po.domain.*;
import com.youthcase.orderflow.po.dto.*;
import com.youthcase.orderflow.po.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🧩 POServiceImpl
 * - 발주(PO) 프로세스의 핵심 비즈니스 로직 구현부
 * - PR(준비) → S(저장) → PO(확정) → GI(출고) → FULLY_RECEIVED(입고완료) 단계 흐름 관리
 * - PriceMaster 스냅샷 기반으로 당시 매입 단가를 고정 저장
 */
@Service
@RequiredArgsConstructor
@Transactional
public class POServiceImpl implements POService {

    private final POHeaderRepository poHeaderRepository;
    private final POItemRepository poItemRepository;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final UserRepository userRepository;

    // ----------------------------------------------------------------------
    // 🔹 공통: 헤더 총합 계산 (아이템 합계 기준)
    // ----------------------------------------------------------------------
    private BigDecimal calculateTotalAmountForHeader(Long poId) {
        return poItemRepository.findByPoHeader_PoId(poId).stream()
                .map(i -> i.getPurchasePrice().multiply(BigDecimal.valueOf(i.getOrderQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ----------------------------------------------------------------------
    // ✅ [1] 상품 추가 or 기존 항목 수량 갱신
    // ----------------------------------------------------------------------
    @Override
    public POItemResponseDTO addOrCreatePOItem(String userId, POItemRequestDTO dto) {
        // 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 현재 진행 중(PR) 장바구니 조회 (없으면 생성)
        Long poId = getCurrentCartId();
        POHeader header = (poId != null)
                ? poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PR 상태 헤더를 찾을 수 없습니다."))
                : createNewPRHeader(user);

        // 상품 및 매입가 스냅샷 조회
        Product product = productRepository.findByGtin(dto.getGtin())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + dto.getGtin()));
        BigDecimal purchasePrice = priceRepository.findPurchasePriceByGtin(dto.getGtin())
                .orElseThrow(() -> new IllegalArgumentException("Price not found for GTIN: " + dto.getGtin()));

        // 기존 동일 GTIN 존재 여부 확인 후 처리
        POItem targetItem = poItemRepository.findByPoHeader_PoIdAndProduct_Gtin(header.getPoId(), dto.getGtin())
                .map(item -> { // 존재 → 수량 증가 및 단가 갱신
                    item.setOrderQty(item.getOrderQty() + dto.getOrderQty());
                    item.setPurchasePrice(purchasePrice); // 매입가 스냅샷 갱신
                    item.calculateTotal();
                    return poItemRepository.save(item);
                })
                .orElseGet(() -> { // 신규 아이템
                    POItem newItem = POItem.builder()
                            .poHeader(header)
                            .product(product)
                            .orderQty(dto.getOrderQty())
                            .pendingQty(dto.getOrderQty())
                            .shippedQty(0L)
                            .purchasePrice(purchasePrice)
                            .expectedArrival(LocalDate.now().plusDays(3))
                            .status(POStatus.PR)
                            .build();
                    newItem.calculateTotal();
                    return poItemRepository.save(newItem);
                });

        // 헤더 총합 갱신
        header.setTotalAmount(calculateTotalAmountForHeader(header.getPoId()));
        poHeaderRepository.save(header);

        return POItemResponseDTO.from(targetItem);
    }

    /** 신규 PR 헤더 생성 (공용 장바구니) */
    private POHeader createNewPRHeader(User user) {
        String branchCode = user.getStore().getStoreId();
        LocalDate today = LocalDate.now();
        long countToday = poHeaderRepository.countByActionDateAndBranchCode(today, branchCode);
        String seq = String.format("%02d", countToday + 1);
        String externalId = today.format(DateTimeFormatter.BASIC_ISO_DATE) + branchCode + seq;

        POHeader header = new POHeader();
        header.setUser(user);
        header.setStatus(POStatus.PR);
        header.setActionDate(today);
        header.setExternalId(externalId);
        header.setTotalAmount(BigDecimal.ZERO);
        return poHeaderRepository.save(header);
    }

    // ----------------------------------------------------------------------
    // ✅ [2] 조회 / 수정 / 삭제
    // ----------------------------------------------------------------------
    @Override
    public List<POHeaderResponseDTO> findAll() {
        return poHeaderRepository.findAll().stream()
                .map(POHeaderResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<POItemResponseDTO> getAllItems(Long poId) {
        return poItemRepository.findByPoHeader_PoId(poId).stream()
                .map(POItemResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public POItemResponseDTO updateItemQuantity(Long itemNo, POItemRequestDTO requestDTO) {
        POItem item = poItemRepository.findByItemNoAndStatus(itemNo, POStatus.PR)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다."));
        if (requestDTO.getOrderQty() < 1)
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");

        item.setOrderQty(requestDTO.getOrderQty());
        item.calculateTotal();
        poItemRepository.save(item);
        return POItemResponseDTO.from(item);
    }

    @Override
    public void deleteItem(List<Long> itemNos) {
        poItemRepository.deleteAllById(itemNos);
    }

    // ----------------------------------------------------------------------
    // ✅ [3] 장바구니 저장/불러오기/삭제
    // ----------------------------------------------------------------------
    @Override
    public void saveCart(Long poId, String remarks) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주 헤더가 존재하지 않습니다."));
        header.setStatus(POStatus.S);
        header.setRemarks(remarks);
        poHeaderRepository.save(header);
    }

    @Override
    public List<POHeaderResponseDTO> getSavedCartList() {
        return poHeaderRepository.findByStatus(POStatus.S).stream()
                .map(POHeaderResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<POItemResponseDTO> getSavedCartItems(Long poId) {
        return poItemRepository.findByPoHeader_PoId(poId).stream()
                .map(POItemResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePO(Long poId) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("POHeader not found: " + poId));
        poHeaderRepository.delete(header);
    }

    // ----------------------------------------------------------------------
    // ✅ [4] 상태 전환 로직 (S → PO → GI → FULLY_RECEIVED)
    // ----------------------------------------------------------------------
    @Override
    public void confirmOrder(Long poId) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주가 존재하지 않습니다."));
        header.setStatus(POStatus.PO);
        poHeaderRepository.save(header);
    }

    @Override
    public void updateReceiveProgress(Long poId) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("POHeader not found: " + poId));

        boolean allReceived = header.getItems().stream()
                .allMatch(item -> item.getPendingQty() != null && item.getPendingQty() == 0);

        header.setStatus(allReceived ? POStatus.FULLY_RECEIVED : POStatus.GI);
        poHeaderRepository.save(header);
    }

    // ----------------------------------------------------------------------
    // ✅ [5] 현재 PR 헤더 조회 (REQUIRES_NEW 트랜잭션)
    // ----------------------------------------------------------------------
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long getCurrentCartId() {
        // 1️⃣ PR 상태 헤더 전체 조회 (최신순)
        List<POHeader> prHeaders = poHeaderRepository.findRecentByStatus(POStatus.PR);

        if (prHeaders.isEmpty()) {
            return null; // PR 상태 없음
        }

        // 2️⃣ 최신 1건 추출(POStatus.PR은 항상 1건만 존재해야 함)
        POHeader latest = prHeaders.get(0);

        // 3️⃣ 나머지 PR 상태 헤더는 S 상태로 변경(POStatus.PR이 여러 건이 존재하는 경우, 최신 PR을 제외한 나머지 저장 처리)
        if (prHeaders.size() > 1) {
            poHeaderRepository.updateStatusExceptOne(POStatus.PR, POStatus.S, latest.getPoId());
        }

        // 4️⃣ 최신 헤더 ID 반환
        return latest.getPoId();
    }
}
