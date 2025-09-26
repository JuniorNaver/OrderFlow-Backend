package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.domain.PR;
import com.youthcase.orderflow.pr.service.PRService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pr")
public class PRController {

    private final PRService prService;

    public PRController(PRService prService) {
        this.prService = prService;
    }

    @GetMapping
    public List<PR> getAll() {
        return prService.findAll();
    }

    @PostMapping
    public PR create(@RequestBody PR pr) {
        return prService.save(pr);
    }
}
