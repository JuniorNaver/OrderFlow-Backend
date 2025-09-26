package com.youthcase.orderflow.stk.controller;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.service.STKService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stk")
public class STKController {

    private final STKService stkService;

    public STKController(STKService stkService) {
        this.stkService = stkService;
    }

    @GetMapping
    public List<STK> getAll() {
        return stkService.findAll();
    }

    @PostMapping
    public STK create(@RequestBody STK stk) {
        return stkService.save(stk);
    }
}
