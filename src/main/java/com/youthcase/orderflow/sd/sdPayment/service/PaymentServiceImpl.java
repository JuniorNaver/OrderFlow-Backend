package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentHeaderRepository headerRepository;

    @Override
    public PaymentHeader createPayment(PaymentHeader header) {

        PaymentRequest request = PaymentRequest.builder()
                .orderId(header.getOrderId())
                .amount(header.getTotalAmount())
                .method(header.getPaymentStatus()) //card/easy/cash
                .build();

        PaymentResult result = processor.processPayment(request);
        if(!result.isSuccess()) {
            throw new RuntimeException("결제 실패: " + result.getMessage());
        }

        header.setTransactionNo(result.getTransactionNo());
        header.setPaymentStatus("APPROVED");
        return headerRepository.save(header);
    }

    @Override
    public PaymentHeader getPayment(Long id) {
        return headerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결제내역 못찾음"));
    }

    @Override
    public void cancelPayment(Long id) {
        PaymentHeader header = getPayment(id);
        header.setPaymentStatus("CANCELED");
        header.setCanceledTime(LocalDateTime.now());
        headerRepository.save(header);
    }
}
