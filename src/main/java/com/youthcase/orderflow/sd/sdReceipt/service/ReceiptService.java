package com.youthcase.orderflow.sd.sdReceipt.service;

import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptRequestDTO;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponseDTO;

public interface ReceiptService {

    /**
     * 결제 완료 후 영수증 생성
     */
    ReceiptResponseDTO createReceipt(Long paymentId);

    /**
     * 영수증 단건 조회 (영수증 ID 기준)
     */
    ReceiptResponseDTO getReceipt(Long receiptId);

    /**
     * 결제 ID 기준으로 영수증 조회
     */
    ReceiptResponseDTO getByPaymentId(Long paymentId);
}
