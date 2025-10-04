package com.youthcase.orderflow.sd.sdPayment.controller;


import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResponse;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 생성
    @PostMapping
    public ResponseEntity<PaymentResult> createPayment(@RequestBody PaymentRequest request) {
        PaymentResult result = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 결제 조회
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        PaymentHeader header = paymentService.getPayment(id);
        return ResponseEntity.ok(PaymentResponse.from(header));
    }

    // 결제 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable long id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.noContent().build();
    }
}
