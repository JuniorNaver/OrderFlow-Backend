package com.youthcase.orderflow.bi.controller;

import com.youthcase.orderflow.bi.domain.BI;
import com.youthcase.orderflow.bi.service.BIService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bi")
public class BIController {

    private final BIService biService;

    public BIController(BIService biService) {
        this.biService = biService;
    }

    @GetMapping
    public List<BI> getAll() {
        return biService.findAll();
    }

    @PostMapping
    public BI create(@RequestBody BI bi) {
        return biService.save(bi);
    }
}
