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
        log.info("💳 간편결제 요청 - impUid: {}", request.getImpUid());

        try {
            // ✅ 1. 아임포트 서버에서 결제 정보 조회
            JsonNode paymentInfo = iamportRestClient.getPaymentByImpUid(request.getImpUid());
            JsonNode response = paymentInfo.get("response");

            if (response == null) {
                log.error("❌ 결제 응답이 비어있습니다. impUid={}", request.getImpUid());
                return PaymentResult.builder()
                        .success(false)
                        .message("결제 응답이 비어있습니다.")
                        .method(PaymentMethod.EASY)
                        .orderId(request.getOrderId())
                        .build();
            }

            // ✅ 2. 응답 데이터 파싱
            String status = response.get("status").asText("");        // paid, ready, failed 등
            BigDecimal amount = new BigDecimal(response.get("amount").asText("0"));
            String pgProvider = response.get("pg_provider").asText(""); // kakao, toss 등
            String impUid = response.get("imp_uid").asText("");
            String merchantUid = response.get("merchant_uid").asText("");

            log.info("✅ 결제 상태: {}, PG사: {}, 금액: {}", status, pgProvider, amount);

            // ✅ 3. 성공 여부 판별 및 결과 반환
            boolean isSuccess = "paid".equalsIgnoreCase(status);

            return PaymentResult.builder()
                    .success(isSuccess)
                    .message(isSuccess ? "간편결제 성공" : "간편결제 실패 (" + status + ")")
                    .transactionId(impUid)               // 아임포트 거래 고유 ID
                    .method(PaymentMethod.EASY)
                    .orderId(request.getOrderId())
                    .paidAmount(amount)
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