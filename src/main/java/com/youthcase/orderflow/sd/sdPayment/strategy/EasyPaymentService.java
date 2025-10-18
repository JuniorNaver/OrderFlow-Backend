package com.youthcase.orderflow.sd.sdPayment.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.youthcase.orderflow.sd.sdPayment.client.IamportRestClient;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service("easy")
@RequiredArgsConstructor
public class EasyPaymentService implements PaymentStrategy {

    private final IamportRestClient iamportRestClient;

    @Override
    public PaymentResult pay(PaymentRequest request) {
        log.info("💳 간편결제 요청 - impUid: {}, provider: {}", request.getImpUid(), request.getProvider());

        try {
            String impUid = request.getImpUid();

            // ✅ 테스트 모드 (IMP_TEST_xxx) → 검증 스킵
            if (impUid == null || impUid.startsWith("IMP_TEST_")) {
                log.warn("⚠️ 테스트 impUid 감지 → 아임포트 검증 스킵, mock 결제 처리");
                String mockImpUid = impUid != null ? impUid : "IMP_TEST_" + System.currentTimeMillis();

                return PaymentResult.builder()
                        .success(true)
                        .message("테스트 간편결제 성공 (mock)")
                        .impUid(mockImpUid)
                        .method(PaymentMethod.EASY)
                        .orderId(request.getOrderId())
                        .paidAmount(request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO)
                        .transactionNo("MOCK_TX_" + mockImpUid.substring(mockImpUid.length() - 6))
                        .build();
            }

            // ✅ 실제 imp_uid 검증 로직
            JsonNode paymentInfo = iamportRestClient.getPaymentByImpUid(impUid);
            JsonNode response = paymentInfo != null ? paymentInfo.get("response") : null;

            if (response == null) {
                log.error("❌ 결제 응답이 비어있습니다. impUid={}", impUid);
                return PaymentResult.builder()
                        .success(false)
                        .message("결제 응답이 비어있습니다.")
                        .method(PaymentMethod.EASY)
                        .orderId(request.getOrderId())
                        .build();
            }

            // ✅ 응답 데이터 파싱
            String status = response.get("status").asText("");
            BigDecimal amount = new BigDecimal(response.get("amount").asText("0"));
            String pgProvider = response.get("pg_provider").asText("");

            log.info("✅ 결제 상태: {}, PG사: {}, 금액: {}", status, pgProvider, amount);

            boolean isSuccess = "paid".equalsIgnoreCase(status);

            return PaymentResult.builder()
                    .success(isSuccess)
                    .message(isSuccess ? "간편결제 성공" : "간편결제 실패 (" + status + ")")
                    .impUid(impUid)
                    .method(PaymentMethod.EASY)
                    .orderId(request.getOrderId())
                    .paidAmount(amount)
                    .transactionNo("TXN-" + System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("❌ 간편결제 처리 중 예외 발생: {}", e.getMessage(), e);
            return PaymentResult.builder()
                    .success(false)
                    .message("간편결제 중 오류 발생: " + e.getMessage())
                    .method(PaymentMethod.EASY)
                    .orderId(request.getOrderId())
                    .build();
        }
    }


    @Override
    public void cancel(PaymentItem item) {
        if (item == null) {
            log.warn("❌ 간편결제 취소 실패: PaymentItem이 null입니다.");
            return;
        }

        log.info("🚫 간편결제 취소 요청 - impUid={}", item.getTransactionNo());
        // TODO: 추후 아임포트 REST API /payments/cancel 호출 구현 예정
    }
}
