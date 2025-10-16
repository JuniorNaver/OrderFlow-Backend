package com.youthcase.orderflow.sd.sdReceipt.service;

import com.youthcase.orderflow.branch.domain.BranchInfo; // ✅ 누락된 import 추가
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdReceipt.domain.Receipt;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponse;
import com.youthcase.orderflow.sd.sdReceipt.repository.ReceiptRepository;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    /**
     * ✅ 영수증 생성
     * Sales, Payment, Refund, Branch 데이터를 합쳐서 하나의 Receipt로 저장
     */
    @Transactional
    public Receipt createReceipt(SalesHeader sales, PaymentHeader payment,
                                 RefundHeader refund, BranchInfo branch) {

        Receipt receipt = Receipt.builder()
                .salesHeader(sales)
                .paymentHeader(payment)
                .refundHeader(refund)
                .branchInfo(branch)
                .build();

        return receiptRepository.save(receipt);
    }

    /**
     * ✅ 날짜별 영수증 조회
     * (모든 DB에서 호환 가능한 방식: issuedAt BETWEEN start~end)
     */
    @Transactional(readOnly = true)
    public List<ReceiptResponse> getReceiptsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return receiptRepository.findByIssuedDateRange(start, end).stream()
                .map(ReceiptResponse::fromEntity)
                .toList();
    }

    /**
     * ✅ 영수증 번호로 조회 (재발행용)
     */
    @Transactional(readOnly = true)
    public Receipt findByReceiptNo(String receiptNo) {
        return receiptRepository.findByReceiptNo(receiptNo)
                .orElseThrow(() -> new RuntimeException("❌ 영수증을 찾을 수 없습니다: " + receiptNo));
    }

    /**
     * ✅ 50일 이상 지난 영수증 자동 삭제 (매일 새벽 3시)
     */
    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldReceipts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(50);
        int deleted = receiptRepository.deleteOldReceipts(cutoff);
        log.info("🧾 영수증 정리 완료 — {}건의 50일 초과 데이터 삭제됨 (기준일: {})", deleted, cutoff);
    }
}