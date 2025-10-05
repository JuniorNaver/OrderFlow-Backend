package com.youthcase.orderflow.sd.sdRefund.service;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundRequestDTO;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface RefundService {
    // 1️⃣ 환불 생성
    RefundResponseDTO createRefund(RefundRequestDTO request);

    // 2️⃣ 환불 상태별 조회
    List<RefundResponseDTO> getRefundsByStatus(RefundStatus status);

    // 3️⃣ 결제건별 환불 내역 조회
    List<RefundResponseDTO> getRefundsByPaymentId(Long paymentId);

    // 4️⃣ 기간별 환불 조회
    List<RefundResponseDTO> getRefundsBetween(LocalDateTime start, LocalDateTime end);
}
