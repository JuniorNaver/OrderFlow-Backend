package com.youthcase.orderflow.gr.controller;

import com.youthcase.orderflow.gr.domain.GR;
import com.youthcase.orderflow.gr.service.GRService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gr")
public class GRController {

    private final GRService grService;

    public GRController(GRService grService) {
        this.grService = grService;
    }

    @GetMapping
    public List<GR> getAll() {
        return grService.findAll();
    }

    @PostMapping
    public GR create(@RequestBody GR gr) {
        return grService.save(gr);
    }
}
