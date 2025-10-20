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

        SalesItem item = new SalesItem();
        item.setProduct(product);
        item.setStk(stk);
        item.setSalesQuantity(request.getQuantity());
        item.setSdPrice(request.getPrice());
        item.setSubtotal(request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

        // ✅ 핵심: Header에 아이템을 추가하여 양방향 관계 유지
        header.addSalesItem(item);

        // ✅ 총액 갱신
        if (header.getTotalAmount() == null)
            header.setTotalAmount(BigDecimal.ZERO);
        header.setTotalAmount(header.getTotalAmount().add(item.getSubtotal()));

        salesHeaderRepository.saveAndFlush(header); // cascade 덕분에 item 자동 저장됨

        log.info("🧾 상품 추가 완료 - orderNo={}, 상품={}, 수량={}, 금액={}",
                header.getOrderNo(), product.getProductName(), item.getSalesQuantity(), item.getSubtotal());

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
