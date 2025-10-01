/*package com.youthcase.orderflow.sd.sdRefund.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundItem;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.refund.dto.RefundItemDTO;
import com.youthcase.orderflow.sd.sdRefund.refund.dto.RefundRequestDTO;
import com.youthcase.orderflow.sd.sdRefund.refund.dto.RefundResponseDTO;
import com.youthcase.orderflow.sd.sdRefund.repository.RefundHeaderRepository;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.sd.sdSales.repository.SalesItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final RefundHeaderRepository refundHeaderRepository;
    private final PaymentHeaderRepository paymentHeaderRepository;
    private final SalesItemRepository salesItemRepository;

    @Override
    @Transactional
    public RefundResponseDTO createRefund(RefundRequestDTO requestDTO) {
        PaymentHeader payment = paymentHeaderRepository.findById(requestDTO.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제건을 찾을 수 없습니다."));

        RefundHeader refundHeader = RefundHeader.builder()
                .paymentId(payment)
                .refundAmount(requestDTO.getItems().stream()
                        .mapToDouble(RefundItemDTO::getRefundAmount)
                        .sum())
                .refundStatus(RefundStatus.REQUESTED)
                .reason(requestDTO.getReason())
                .requestedTime(LocalDateTime.now())
                .build();

        List<RefundItem> refundItems = requestDTO.getItems().stream()
                .map(dto -> {
                    PaymentItem paymentItem = paymentItemRepository.findById(dto.getPaymentItemId())
                            .orElseThrow(() -> new IllegalArgumentException("판매 아이템을 찾을 수 없습니다."));
                    return RefundItem.builder()
                            .paymentItem(paymentItem)
                            .refundHeader(refundHeader)
                            .refundQuantity(dto.getRefundQuantity())
                            .refundAmount(dto.getRefundAmount())
                            .build();
                })
                .toList();
        refundHeader.setRefundItems(refundItems);
        RefundHeader saved = refundHeaderRepository.save(refundHeader);

        return RefundResponseDTO.builder()
                .refundId(saved.getRefundId())
                .paymentId(saved.getPayment().getPaymentId())
                .refundAmount(saved.getRefundAmount())
                .refundStatus(saved.getRefundStatus())
                .reason(saved.getReason())
                .requestedTime(saved.getRequstedTime())
                .approvedTime(saved.getApprovedTime())
                .items(
                        saved.getRefundItems().stream()
                                .map(i -> new RefundItemDTO(
                                        i.getSalesItems().getId(),
                                        i.getRefundQuantity(),
                                        i.getRefundAmount()
                                )).toList()
                )
                .build();
    }

    @Override
    public RefundResponseDTO getRefund(Long refundId) {
        RefundHeader refundHeader = refundHeaderRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 정보를 찾을 수 없습니다."));

        return RefundResponseDTO.builder()
                .refundId(refundHeader.getRefundId())
                .paymentId(refundHeader.getPayment().getPaymentId())
                .refundAmount(refundHeader.getRefundAmount())
                .refundStatus(refundHeader.getRefundStatus())
                .reason(refundHeader.getReason())
                .requestedTime(refundHeader.getRequestedTime())
                .approvedTime(refundHeader,getApprovedTime())
                .items(
                        refundHeader.getRefundItems().stream()
                                .map(i -> new RefundItemDTO(
                                        i.getSalesItem().getId(),
                                        i.getRefundQuantity(),
                                        i.getRefundAmount()
                                )).toList()
                )
                .build();
    }

}
*/