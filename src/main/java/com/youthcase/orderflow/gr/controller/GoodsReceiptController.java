package com.youthcase.orderflow.gr.controller;

import com.youthcase.orderflow.gr.dto.GoodsReceiptHeaderDTO;
import com.youthcase.orderflow.gr.service.GoodsReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gr")
@RequiredArgsConstructor
public class GoodsReceiptController {

    private final GoodsReceiptService service;

    @PostMapping
    public ResponseEntity<GoodsReceiptHeaderDTO> create(@RequestBody GoodsReceiptHeaderDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsReceiptHeaderDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
