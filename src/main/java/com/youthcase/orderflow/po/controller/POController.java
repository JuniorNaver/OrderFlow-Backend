package com.youthcase.orderflow.po.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/po")
public class POController {

    private final POHeaderService poHeaderService;

    public POController(POHeaderService poHeaderService) {
        this.poHeaderService = poHeaderService;
    }

    @GetMapping
    public List<PO> getAll() {
        return poHeaderService.findAll();
    }

    @PostMapping
    public PO create(@RequestBody PO po) {
        return poHeaderService.save(po);
    }
}
