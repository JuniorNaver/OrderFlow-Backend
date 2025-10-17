package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.sd.sdPayment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentResult;

public interface PaymentService {

    PaymentResult createPayment(PaymentRequest request);

    void savePayment(PaymentResult result);

    PaymentResult cancelPayment(Long paymentItemId);

}
