package com.youthcase.orderflow.sd.sdPayment.payment.dto;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EasyPaymentRequest {
    private String provider; // kakao / toss
    private String orderId;
    private String userId;
    private String itemName;
    private Long amount;
    private String successUrl;
    private String failUrl;
    private String cancelUrl;
}