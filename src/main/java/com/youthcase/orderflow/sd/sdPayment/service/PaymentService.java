package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;

public interface PaymentService {
    PaymentResult createPayment(PaymentRequest request);
    PaymentHeader getPayment(Long id);
    void cancelPayment(Long id);


}
