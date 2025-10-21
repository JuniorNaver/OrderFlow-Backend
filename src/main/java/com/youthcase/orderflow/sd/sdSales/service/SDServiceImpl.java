package com.youthcase.orderflow.sd.sdSales.service;

import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentStatus;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SDServiceImpl implements SDService {

    private final SalesHeaderRepository salesHeaderRepository;
    private final SalesItemRepository salesItemRepository;
    private final ProductRepository productRepository;
    private final STKRepository stkRepository;
    private final PaymentHeaderRepository paymentHeaderRepository;
    private final StoreRepository storeRepository;

    // ✅ 주문 생성
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SalesHeader createOrder(String storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("점포 정보를 찾을 수 없습니다: " + storeId));

        // ✅ 오늘 날짜
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // ✅ 오늘 날짜로 시작하는 마지막 주문번호 조회
        String lastOrderNo = salesHeaderRepository.findLastOrderNoByDate(datePrefix);

        // ✅ 시퀀스 증가 로직
        int nextSeq = 1;
        if (lastOrderNo != null && lastOrderNo.length() >= 13) { // 예: 20251020-0012
            String seqStr = lastOrderNo.substring(9); // "0012"
            nextSeq = Integer.parseInt(seqStr) + 1;
        }

        // ✅ 새 주문번호 생성 (4자리 패딩)
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

        STK stk = stkRepository
                .findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(request.getGtin(), 0)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 상품의 재고가 없습니다."));

        SalesItem existingItem = salesItemRepository.findByOrderIdAndGtin(
                request.getOrderId(), request.getGtin()
        );

        SalesItem item;
        if (existingItem != null) {
            int newQty = existingItem.getSalesQuantity() + request.getQuantity();
            BigDecimal newSubtotal = existingItem.getSdPrice()
                    .multiply(BigDecimal.valueOf(newQty));

            existingItem.setSalesQuantity(newQty);
            existingItem.setSubtotal(newSubtotal);
            item = existingItem;
            log.info("♻️ 기존 상품 갱신 - {}, {}", product.getProductName(), newQty);
        } else {
            item = new SalesItem();
            item.setProduct(product);
            item.setStk(stk);
            item.setSalesQuantity(request.getQuantity());
            item.setSdPrice(request.getPrice());
            item.setSubtotal(request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            header.addSalesItem(item);
            log.info("🆕 신규 상품 추가 - {}, {}", product.getProductName(), item.getSalesQuantity());
        }

        BigDecimal newTotal = header.getSalesItems().stream()
                .map(SalesItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        header.setTotalAmount(newTotal);


        salesItemRepository.saveAndFlush(item);

        log.info("✅ addItemToOrder 완료: itemId={}, orderNo={}", item.getNo(), header.getOrderNo());
        return SalesItemDTO.from(item);
    }





    // ✅ 주문 확정
    @Override
    @Transactional
    public void confirmOrder(ConfirmOrderRequest request) {
        SalesHeader header = salesHeaderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        // ✅ 주문 상태 갱신
        header.setSalesStatus(SalesStatus.COMPLETED);
        header.setSalesDate(LocalDateTime.now());

        if (request.getTotalAmount() != null) {
            header.setTotalAmount(request.getTotalAmount());
        }

        // ✅ 아이템 목록 추가 (요청에 포함된 경우)
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (ConfirmOrderRequest.ItemDTO dto : request.getItems()) {
                Product product = productRepository.findById(dto.getGtin())
                        .orElseThrow(() -> new RuntimeException("상품 없음: " + dto.getGtin()));

                SalesItem item = new SalesItem();
                item.setSalesHeader(header);
                item.setProduct(product);
                item.setSalesQuantity(dto.getQuantity());
                item.setSdPrice(dto.getPrice());
                item.setSubtotal(dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

                salesItemRepository.save(item);
            }
        }

        salesHeaderRepository.save(header);
    }

    // ✅ 주문 아이템 조회
    @Override
    public List<SalesItemDTO> getItemsByOrderId(Long orderId) {
        return salesItemRepository.findItemsByHeaderId(orderId);
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long itemId, int quantity) {
        SalesItem item = salesItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("판매 항목을 찾을 수 없습니다."));

        BigDecimal subtotal = item.getSdPrice().multiply(BigDecimal.valueOf(quantity));

        // ✅ 직접 DB 업데이트 (즉시 쿼리 실행)
        salesItemRepository.updateQuantity(itemId, quantity, subtotal);

        // ✅ 헤더 금액 갱신
        SalesHeader header = item.getSalesHeader();
        BigDecimal newTotal = header.getSalesItems().stream()
                .map(i -> {
                    // ✅ 잘못된 비교 수정 (i.getNo → i.getId)
                    if (i.getNo().equals(itemId)) {
                        return subtotal; // 바뀐 항목은 새 subtotal로 계산
                    }
                    return i.getSubtotal();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        header.setTotalAmount(newTotal);
        salesHeaderRepository.save(header);

        log.info("🧾 수량 변경 완료(DB 반영) - itemId={}, qty={}, subtotal={}, headerTotal={}",
                itemId, quantity, subtotal, newTotal);
    }


    // ✅ 주문 완료 처리
    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        PaymentHeader paymentHeader = paymentHeaderRepository
                .findFirstBySalesHeader_OrderIdOrderByPaymentIdDesc(orderId)
                .orElseThrow(() -> new RuntimeException("결제 내역 없음"));

        if (paymentHeader.getPaymentStatus() != PaymentStatus.APPROVED) {
            throw new IllegalStateException("💰 결제가 모두 완료되지 않았습니다.");
        }

        BigDecimal totalAmount = salesItemRepository.findItemsByHeaderId(orderId)
                .stream()
                .map(SalesItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        header.setTotalAmount(totalAmount);
        header.setSalesStatus(SalesStatus.COMPLETED);
        header.setSalesDate(LocalDateTime.now());

        salesHeaderRepository.save(header);
        log.info("✅ 주문 {} 결제 완료 및 판매 확정됨 (총액: ₩{})", orderId, totalAmount);
    }

    // ✅ 보류 처리
    @Override
    @Transactional
    public void holdOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        if (!SalesStatus.PENDING.equals(header.getSalesStatus())) {
            throw new RuntimeException("진행 중인 주문만 보류할 수 있습니다.");
        }

        header.setSalesStatus(SalesStatus.HOLD);
        salesHeaderRepository.save(header);
    }

    // ✅ 보류 목록 조회
    @Override
    public List<SalesHeaderDTO> getHoldOrders() {
        return salesHeaderRepository.findHoldOrders();
    }

    // ✅ 보류 주문 다시 열기
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

        List<SalesItemDTO> items = salesItemRepository.findItemsByHeaderId(orderId);
        dto.setSalesItems(items);
        return dto;
    }

    // ✅ 주문 저장/업데이트
    @Override
    @Transactional
    public void saveOrUpdateOrder(Long orderId, List<SalesItemDTO> items, SalesStatus status) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        salesItemRepository.deleteBySalesHeader(header);

        for (SalesItemDTO dto : items) {
            Product product = productRepository.findByProductName(dto.getProductName())
                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

            STK stk = stkRepository.findTopByProduct_Gtin(product.getGtin()).orElse(null);

            SalesItem item = new SalesItem();
            item.setProduct(product);
            item.setStk(stk);
            item.setSalesQuantity(dto.getSalesQuantity());
            item.setSdPrice(dto.getSdPrice());
            item.setSubtotal(dto.getSdPrice().multiply(BigDecimal.valueOf(dto.getSalesQuantity())));

            // ✅ 핵심: 양방향 연결
            header.addSalesItem(item);
        }

        BigDecimal total = items.stream()
                .map(i -> i.getSdPrice().multiply(BigDecimal.valueOf(i.getSalesQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        header.setTotalAmount(total);
        header.setSalesStatus(status);
        header.setSalesDate(LocalDateTime.now());

        salesHeaderRepository.save(header);
        log.info("💾 주문 {} 저장 완료 (상태: {}, 총액: ₩{})", orderId, status, total);
    }

    // ✅ 보류 주문 취소
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("보류 주문 없음"));

        if (!SalesStatus.HOLD.equals(header.getSalesStatus())) {
            throw new RuntimeException("보류 상태가 아닌 주문은 취소 불가");
        }

        header.setSalesStatus(SalesStatus.CANCELLED);
        salesHeaderRepository.save(header);
    }
}
