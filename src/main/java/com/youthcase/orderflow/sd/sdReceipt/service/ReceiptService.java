package com.youthcase.orderflow.sd.sdReceipt.service;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdReceipt.domain.Receipt;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponse;
import com.youthcase.orderflow.sd.sdReceipt.repository.ReceiptRepository;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final SalesHeaderRepository salesHeaderRepository; // ✅ 추가

    /**
     * ✅ 영수증 생성
     * - SalesHeader를 영속 상태로 재로딩하여 SalesItem lazy 문제 해결
     */
    @Transactional
    public Receipt createReceipt(SalesHeader sales, PaymentHeader payment,
                                 RefundHeader refund, Store store) {

        // 🔹 SalesHeader를 다시 영속화
        SalesHeader managedSales = salesHeaderRepository.findById(sales.getOrderId())
                .orElseThrow(() -> new IllegalStateException("SalesHeader를 찾을 수 없습니다."));

        // 🔹 영수증 생성
        Receipt receipt = Receipt.builder()
                .salesHeader(managedSales)
                .paymentHeader(payment)
                .refundHeader(refund)
                .store(store)
                .build();

        Receipt saved = receiptRepository.save(receipt);
        log.info("🧾 영수증 저장 완료 - receiptNo={}, orderNo={}, items={}개",
                saved.getReceiptNo(),
                managedSales.getOrderNo(),
                managedSales.getSalesItems().size());

        return saved;
    }

    /**
     * ✅ 날짜별 영수증 조회 (fetch join 적용)
     */
    @Transactional(readOnly = true)
    public List<ReceiptResponse> getReceiptsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return receiptRepository.findWithDetailsByIssuedDateRange(start, end).stream()
                .map(ReceiptResponse::fromEntity)
                .toList();
    }

    /**
     * ✅ 영수증 번호로 조회 (재발행용)
     */
    @Transactional(readOnly = true)
    public Receipt findByReceiptNo(String receiptNo) {
        return receiptRepository.findWithDetailsByReceiptNo(receiptNo)
                .orElseThrow(() -> new RuntimeException("❌ 영수증을 찾을 수 없습니다: " + receiptNo));
    }

    /**
     * ✅ 50일 이상 지난 영수증 자동 삭제
     */
    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldReceipts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(50);
        int deleted = receiptRepository.deleteOldReceipts(cutoff);
        log.info("🧾 영수증 정리 완료 — {}건 삭제됨 (기준일: {})", deleted, cutoff);
    }

    /**
     * ✅ 통합 영수증 (결제 여러 개일 때 대표 헤더 1개 연결)
     */
    @Transactional
    public Receipt createUnifiedReceipt(SalesHeader sales, Store store) {
        // 중복 방지
        Receipt existing = receiptRepository.findBySalesHeader(sales.getOrderId()).orElse(null);
        if (existing != null) {
            log.info("⚠️ 이미 생성된 영수증 존재 - orderNo={}", sales.getOrderNo());
            return existing;
        }

        SalesHeader managedSales = salesHeaderRepository.findById(sales.getOrderId())
                .orElseThrow(() -> new IllegalStateException("SalesHeader를 찾을 수 없습니다."));

        BigDecimal totalAmount = managedSales.getPaymentHeaders().stream()
                .flatMap(ph -> ph.getPaymentItems().stream())
                .map(PaymentItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentHeader mainHeader = managedSales.getPaymentHeaders().stream().findFirst().orElse(null);

        Receipt receipt = Receipt.builder()
                .salesHeader(managedSales)
                .paymentHeader(mainHeader)
                .refundHeader(null)
                .store(store)
                .build();

        Receipt saved = receiptRepository.save(receipt);
        log.info("🧾 통합 영수증 생성 완료 - orderNo={}, total={}, items={}",
                managedSales.getOrderNo(), totalAmount, managedSales.getSalesItems().size());

        return saved;
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptForReissue(String receiptNo, LocalDateTime cutoff) {
        var receipt = receiptRepository.findWithDetailsByReceiptNo(receiptNo)
                .orElseThrow(() -> new RuntimeException("❌ 영수증을 찾을 수 없습니다: " + receiptNo));

        if (receipt.getIssuedAt().isBefore(cutoff)) {
            throw new IllegalStateException("❌ 50일 이상 지난 영수증은 재발행 불가");
        }

        return ReceiptResponse.fromEntity(receipt);
    }

}
