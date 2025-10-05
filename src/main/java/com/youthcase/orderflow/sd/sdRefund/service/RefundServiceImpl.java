package com.youthcase.orderflow.sd.sdRefund.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundItem;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundItemDTO;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundRequestDTO;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponseDTO;
import com.youthcase.orderflow.sd.sdRefund.repository.RefundHeaderRepository;
import com.youthcase.orderflow.sd.sdRefund.repository.RefundItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundServiceImpl implements RefundService {

    private final RefundHeaderRepository refundHeaderRepository;
    private final RefundItemRepository refundItemRepository;
    private final PaymentHeaderRepository paymentHeaderRepository;

    /**
     * 1️⃣ 환불 생성
     */
    @Override
    public RefundResponseDTO createRefund(RefundRequestDTO request) {
        PaymentHeader paymentHeader = paymentHeaderRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // 환불 총액 계산
        BigDecimal totalRefundAmount = request.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getRefundAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // RefundHeader 생성
        RefundHeader header = RefundHeader.builder()
                .paymentHeader(paymentHeader)
                .refundAmount(totalRefundAmount)
                .refundStatus(RefundStatus.REQUESTED)
                .requestedTime(LocalDateTime.now())
                .reason(request.getReason())
                .build();

        RefundHeader savedHeader = refundHeaderRepository.save(header);

        // RefundItem 생성
        List<RefundItem> refundItems = request.getItems().stream()
                .map(itemDTO -> {
                    PaymentItem paymentItem = paymentHeader.getPaymentItems().stream()
                            .filter(pi -> pi.getPaymentItemId().equals(itemDTO.getPaymentItemId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("해당 결제 아이템을 찾을 수 없습니다."));

                    return RefundItem.builder()
                            .refundHeader(savedHeader)
                            .paymentItem(paymentItem)
                            .salesItem(paymentItem.getSalesItem())
                            .refundAmount(BigDecimal.valueOf(itemDTO.getRefundAmount()))
                            .refundReason(request.getReason())
                            .transactionNo(paymentItem.getTransactionNo())
                            .build();
                }).collect(Collectors.toList());

        refundItemRepository.saveAll(refundItems);

        // DTO 변환 후 반환
        return RefundResponseDTO.builder()
                .refundId(savedHeader.getRefundId())
                .paymentId(paymentHeader.getPaymentId())
                .refundAmount(totalRefundAmount.doubleValue())
                .refundStatus(savedHeader.getRefundStatus())
                .reason(savedHeader.getReason())
                .requestedTime(savedHeader.getRequestedTime())
                .approvedTime(savedHeader.getApprovedTime())
                .items(request.getItems())
                .build();
    }

    /**
     * 2️⃣ 상태별 환불 조회
     */
    @Override
    public List<RefundResponseDTO> getRefundsByStatus(RefundStatus status) {
        return refundHeaderRepository.findByRefundStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 3️⃣ 결제건별 환불 조회
     */
    @Override
    public List<RefundResponseDTO> getRefundsByPaymentId(Long paymentId) {
        return refundHeaderRepository.findByPaymentHeader_PaymentId(paymentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 4️⃣ 기간별 환불 조회
     */
    @Override
    public List<RefundResponseDTO> getRefundsBetween(LocalDateTime start, LocalDateTime end) {
        return refundHeaderRepository.findByRequestedTimeBetween(start, end)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Entity → DTO 변환 메서드
     */
    private RefundResponseDTO convertToDTO(RefundHeader header) {
        List<RefundItemDTO> items = header.getRefundItems().stream()
                .map(item -> RefundItemDTO.builder()
                        .paymentItemId(item.getPaymentItem().getPaymentItemId())
                        .refundQuantity(1) // 수량 로직 확장 가능
                        .refundAmount(item.getRefundAmount().intValue())
                        .build())
                .collect(Collectors.toList());

        return RefundResponseDTO.builder()
                .refundId(header.getRefundId())
                .paymentId(header.getPaymentHeader().getPaymentId())
                .refundAmount(header.getRefundAmount().doubleValue())
                .refundStatus(header.getRefundStatus())
                .reason(header.getReason())
                .requestedTime(header.getRequestedTime())
                .approvedTime(header.getApprovedTime())
                .items(items)
                .build();
    }
}