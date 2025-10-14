package com.youthcase.orderflow.sd.sdPayment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.youthcase.orderflow.sd.sdPayment.client.IamportRestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamportService {

    private final IamportRestClient iamportRestClient;

    public JsonNode getPaymentByImpUid(String impUid) {
        log.info("💳 결제정보 조회 요청: impUid={}", impUid);
        JsonNode result = iamportRestClient.getPaymentByImpUid(impUid);
        log.info("✅ 결제정보 응답 수신: {}", result);
        return result;
    }
}