package com.youthcase.orderflow.sd.sdReceipt.service;

import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponseDTO;

public interface ReceiptService {
    ReceiptResponseDTO createReceipt(Long paymentId);
    ReceiptResponseDTO getReceiptByPaymentId(Long paymentId);
    ReceiptResponseDTO getReceipt(Long receiptId);
}
