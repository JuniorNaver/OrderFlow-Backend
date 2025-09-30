package com.youthcase.orderflow.sd.sdPayment.controller;


import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
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

    //결제 생성
    @PostMapping
    public ResponseEntity<PaymentHeader> createPayment(@RequestBody PaymentHeader header,
                                                       @RequestParam String method) {
        header.setPaymentStatus(method); //card/easy/cash
        PaymentHeader saved = paymentService.createPayment(header);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    //결제 조회
    @GetMapping("/{id}")
    public ResponseEntity<PaymentHeader> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    //결제 취소
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelPayment(@PathVariable long id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.noContent().build();
    }
}
