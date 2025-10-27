package com.youthcase.orderflow.sd.sdSales.service;

import com.youthcase.orderflow.master.price.repository.PriceRepository;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import com.youthcase.orderflow.sd.sdSales.domain.*;
import com.youthcase.orderflow.sd.sdSales.dto.AddItemRequest;
import com.youthcase.orderflow.sd.sdSales.dto.ConfirmOrderRequest;
import com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import com.youthcase.orderflow.sd.sdSales.repository.SalesItemRepository;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SDServiceImpl implements SDService {

    private final SalesHeaderRepository salesHeaderRepository;
    private final SalesItemRepository salesItemRepository;
    private final ProductRepository productRepository;
    private final STKRepository stkRepository;
    private final StoreRepository storeRepository;
    private final PriceRepository priceRepository;

    // ✅ 주문 생성
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SalesHeader createOrder(String storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("점포 정보를 찾을 수 없습니다: " + storeId));

        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String lastOrderNo = salesHeaderRepository.findLastOrderNoByDate(datePrefix);

        int nextSeq = 1;
        if (lastOrderNo != null && lastOrderNo.length() >= 13) {
            String seqStr = lastOrderNo.substring(9);
            nextSeq = Integer.parseInt(seqStr) + 1;
        }

        String newOrderNo = String.format("%s-%04d", datePrefix, nextSeq);

        SalesHeader header = new SalesHeader();
        header.setOrderNo(newOrderNo);
        header.setSalesDate(LocalDateTime.now());
        header.setSalesStatus(SalesStatus.PENDING);
        header.setTotalAmount(BigDecimal.ZERO);
        header.setStore(store);

        SalesHeader saved = salesHeaderRepository.saveAndFlush(header);
        log.info("✅ [createOrder] 주문 생성 완료: orderNo={}, storeId={}", saved.getOrderNo(), storeId);
        return saved;
    }

    // ✅ 상품 추가
    @Override
    @Transactional
    public SalesItemDTO addItemToOrder(AddItemRequest request) {
        SalesHeader header = salesHeaderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        Product product = productRepository.findByGtin(request.getGtin())
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        BigDecimal unitPrice = priceRepository.findSalePriceByGtin(request.getGtin())
                .orElse(product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO);

        if (SalesStatus.COMPLETED.equals(header.getSalesStatus())) {
            throw new RuntimeException("COMPLETE 상태에서는 상품을 추가할 수 없습니다.");
        }

        Long totalActiveStock = stkRepository.sumActiveQuantityByGtin(product.getGtin());
        Long reservedInThisOrder = salesItemRepository.sumQuantityByOrderAndGtin(request.getOrderId(), request.getGtin());

        SalesItem item = salesItemRepository.findByOrderIdAndGtin(request.getOrderId(), request.getGtin());
        if (item != null) {
            Long newQty = item.getSalesQuantity() + request.getQuantity();
            item.setSalesQuantity(newQty);
            item.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(newQty)));
        } else {
            item = new SalesItem();
            item.setProduct(product);
            item.setSalesQuantity(request.getQuantity());
            item.setSdPrice(unitPrice);
            item.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(request.getQuantity())));
            item.setStk(null); // ✅ HOLD/PENDING 상태에서는 STK 연결 금지
            header.addSalesItem(item);
        }

        BigDecimal newTotal = header.getSalesItems().stream()
                .map(SalesItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        header.setTotalAmount(newTotal);

        salesItemRepository.saveAndFlush(item);

        SalesItemDTO dto = SalesItemDTO.from(item);
        dto.setStockQuantity(totalActiveStock);

        log.info("🧾 addItemToOrder: orderId={}, gtin={}, 단가={}, 재고={}",
                header.getOrderId(), product.getGtin(), unitPrice, totalActiveStock);


        return dto;
    }

    @Override
    public List<SalesItemDTO> getItemsByOrderId(Long orderId) {
        return salesItemRepository.findItemsByHeaderId(orderId);
    }

    @Override
    @Transactional
    public SalesHeaderDTO deleteItemFromOrder(Long orderId, Long itemId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // ✅ 상태 검사
        if (header.getSalesStatus() == SalesStatus.COMPLETED) {
            throw new IllegalStateException("이미 확정된 주문은 수정할 수 없습니다.");
        }

        // ✅ 삭제 대상 찾기
        SalesItem item = salesItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("삭제할 상품이 존재하지 않습니다."));

        // ✅ 헤더에서 아이템 제거 + Repository 삭제
        header.getSalesItems().remove(item);
        salesItemRepository.delete(item);

        // ✅ 총액 재계산
        BigDecimal newTotal = header.getSalesItems().stream()
                .map(SalesItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        header.setTotalAmount(newTotal);
        salesHeaderRepository.save(header);

        log.info("🗑️ 상품 삭제 완료 — orderId={}, itemId={}, 새 총액={}", orderId, itemId, newTotal);

        return SalesHeaderDTO.from(header);
    }


    // ✅ 주문 확정 (결제 완료 시점)
    @Override
    @Transactional
    public void confirmOrder(ConfirmOrderRequest request) {
        SalesHeader header = salesHeaderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        if (request.getTotalAmount() != null) {
            header.setTotalAmount(request.getTotalAmount());
        }

        // ✅ 각 아이템에 대해 STK 연결 + 재고 차감
        for (SalesItem item : header.getSalesItems()) {
            Long need = item.getSalesQuantity();

            List<STK> stocks = stkRepository
                    .findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(
                            item.getProduct().getGtin(), 0L);

            for (STK stk : stocks) {
                if (need <= 0) break;

                Long available = stk.getQuantity();
                Long deduct = Math.min(available, need);

                stk.setQuantity(available - deduct);
                stkRepository.save(stk);

                // ✅ 판매 아이템과 첫 번째 사용한 STK를 연결
                if (item.getStk() == null) item.setStk(stk);

                need -= deduct;
            }

            if (need > 0) {
                throw new RuntimeException("❌ 재고 부족: " + item.getProduct().getProductName());
            }
        }

        header.setSalesStatus(SalesStatus.COMPLETED);
        header.setSalesDate(LocalDateTime.now());
        salesHeaderRepository.save(header);

        log.info("✅ [confirmOrder] 주문 {} 확정 완료 — 재고 차감 및 상태 COMPLETED", header.getOrderId());
    }

    // ✅ 보류 처리 (재고 차감 금지)
    @Override
    @Transactional
    public void holdOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        if (!SalesStatus.PENDING.equals(header.getSalesStatus())) {
            throw new RuntimeException("진행 중인 주문만 보류할 수 있습니다.");
        }

        // 🔹 재고 차감 금지 — 단순히 상태만 HOLD로 변경
        header.setSalesStatus(SalesStatus.HOLD);
        header.setSalesDate(LocalDateTime.now());
        salesHeaderRepository.save(header);

        log.info("💾 [holdOrder] 주문 {} 보류 저장 완료 (재고 차감 없음)", orderId);
    }

    // ✅ 보류 다시 열기
    @Override
    @Transactional
    public SalesHeaderDTO resumeOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("보류 주문 없음"));

        if (!SalesStatus.HOLD.equals(header.getSalesStatus())) {
            throw new RuntimeException("보류 상태가 아닌 주문은 다시 열 수 없습니다.");
        }

        header.setSalesStatus(SalesStatus.PENDING);

        SalesHeaderDTO dto = new SalesHeaderDTO(
                header.getOrderId(),
                header.getOrderNo(),
                header.getSalesDate(),
                header.getTotalAmount(),
                header.getSalesStatus()
        );

        dto.setSalesItems(salesItemRepository.findItemsByHeaderId(orderId));
        return dto;
    }

    // ✅ 보류 주문 저장 (재고 차감 금지)
    @Override
    @Transactional
    public void saveOrUpdateOrder(Long orderId, List<SalesItemDTO> items, SalesStatus status) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        salesItemRepository.deleteBySalesHeader(header);

        for (SalesItemDTO dto : items) {
            Product product = productRepository.findByProductName(dto.getProductName())
                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

            SalesItem item = new SalesItem();
            item.setProduct(product);
            item.setStk(null); // ✅ 재고 미지정
            item.setSalesQuantity(dto.getSalesQuantity());
            item.setSdPrice(dto.getUnitPrice());
            item.setSubtotal(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getSalesQuantity())));
            header.addSalesItem(item);
        }

        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getSalesQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        header.setTotalAmount(total);
        header.setSalesStatus(status);
        header.setSalesDate(LocalDateTime.now());

        salesHeaderRepository.save(header);
        log.info("💾 [saveOrUpdateOrder] 주문 {} 저장 완료 (상태: {}, 총액: ₩{})", orderId, status, total);
    }

    // ✅ 보류 취소 (재고 복원)
    // ✅ 보류 취소 (재고 복원)
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("보류 주문 없음"));

        if (!SalesStatus.HOLD.equals(header.getSalesStatus())) {
            throw new RuntimeException("보류 상태가 아닌 주문은 취소 불가");
        }

        for (SalesItem item : header.getSalesItems()) {
            if (item.getStk() != null) {
                STK stk = item.getStk();
                stk.setQuantity(stk.getQuantity() + item.getSalesQuantity());
                stkRepository.save(stk);
            }
        }

        header.setSalesStatus(SalesStatus.CANCELLED);
        salesHeaderRepository.save(header);
        log.info("♻️ [cancelOrder] 주문 {} 취소 완료 — 재고 복원됨", orderId);
    }

    // ✅ 보류 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<SalesHeaderDTO> getHoldOrders() {
        List<SalesHeader> holdOrders = salesHeaderRepository.findBySalesStatus(SalesStatus.HOLD);
        return holdOrders.stream()
                .map(SalesHeaderDTO::from)
                .toList();
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long itemId, Long quantity) {
        SalesItem item = salesItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("판매 항목을 찾을 수 없습니다. ID=" + itemId));

        // 🔒 상태 확인
        SalesHeader header = item.getSalesHeader();
        if (header.getSalesStatus() == SalesStatus.COMPLETED) {
            throw new IllegalStateException("확정된 주문의 수량은 수정할 수 없습니다.");
        }

        // ⚠️ 유효성 검사
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }

        // 💰 계산
        BigDecimal price = Optional.ofNullable(item.getSdPrice())
                .orElseThrow(() -> new IllegalStateException("단가 정보가 없습니다."));
        item.setSalesQuantity(quantity);
        item.setSubtotal(price.multiply(BigDecimal.valueOf(quantity)));

        log.info("✏️ 수량 수정 완료 — itemId={}, 변경 수량={}, 변경 후 금액={}",
                itemId, quantity, item.getSubtotal());
    }


}
