package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.dto.PurchaseRequestCreateDto;
import com.youthcase.orderflow.pr.dto.PurchaseRequestDto;
import com.youthcase.orderflow.pr.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pr")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService service;

    @PostMapping("/stores/{storeId}/orders")
    @PreAuthorize("hasAuthority('PR_ORDER') or hasRole('ADMIN')")
    public PurchaseRequestDto placeOrder(
            @PathVariable String storeId,
            @RequestBody PurchaseRequestCreateDto dto,
            Authentication auth
    ) {
        return service.placeOrder(storeId, dto, auth);
    }
}
