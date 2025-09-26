package com.youthcase.orderflow.po.controller;

import com.youthcase.orderflow.po.domain.PO;
import com.youthcase.orderflow.po.service.POService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/po")
public class POController {

    private final POService poService;

    public POController(POService poService) {
        this.poService = poService;
    }

    @GetMapping
    public List<PO> getAll() {
        return poService.findAll();
    }

    @PostMapping
    public PO create(@RequestBody PO po) {
        return poService.save(po);
    }
}
