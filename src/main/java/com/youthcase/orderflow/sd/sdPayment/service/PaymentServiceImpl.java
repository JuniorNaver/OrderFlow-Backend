package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentStatus;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdPayment.payment.PaymentProcessor;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentHeaderRepository headerRepository;
    private final SalesHeaderRepository salesHeaderRepository;
    private final PaymentProcessor processor;

    @Override
    @Transactional
    public PaymentResult createPayment(PaymentRequest request) {

            SalesHeader salesHeader = salesHeaderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없음"));
            // PaymentHeader와 PaymentItem 생성 로직 작성
            PaymentHeader header = new PaymentHeader();
            header.setSalesHeader(salesHeader);
            header.setTotalAmount(request.getAmount());
            header.setPaymentStatus(PaymentStatus.APPROVED);

            PaymentResult result = processor.processPayment(request);
            if (!result.isSuccess()) {
                throw new RuntimeException("결제 실패: " + result.getMessage());
            }

            // DB 저장
            headerRepository.save(header);
            return result;
        }


    @Override
    public PaymentHeader getPayment(Long id) {
        return headerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결제내역 못찾음"));
    }

    @Override
    @Transactional
    public void cancelPayment(Long id) {
        PaymentHeader header = getPayment(id);

        header.getPaymentItems().forEach(item -> {
            processor.cancelPayment(item.getPaymentMethod().getKey(), item);
            // ✅ 실무에서는 transactionNo null 대신 상태 플래그만 변경하는 게 좋음
            item.setTransactionNo(null);
        });

        header.setPaymentStatus(PaymentStatus.CANCELED); // ✅ Enum 사용
        header.setCanceledTime(LocalDateTime.now());
        headerRepository.save(header);
    }
}
