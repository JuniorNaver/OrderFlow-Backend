package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;

public interface PaymentService {
    PaymentHeader createPayment(PaymentHeader header);
    PaymentHeader getPayment(Long id);
    void cancelPayment(Long id);


}
