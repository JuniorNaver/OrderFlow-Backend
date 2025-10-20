package com.youthcase.orderflow.master.price.controller;

import com.youthcase.orderflow.master.price.dto.PriceResponseDTO;
import com.youthcase.orderflow.master.price.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/price")
@RequiredArgsConstructor
public class PriceController {
    private final PriceService priceService;

}