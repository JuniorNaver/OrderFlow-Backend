package com.youthcase.orderflow.sd.controller;

import com.youthcase.orderflow.sd.domain.SD;
import com.youthcase.orderflow.sd.service.SDService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sd")
public class SDController {

    private final SDService sdService;

    public SDController(SDService sdService) {
        this.sdService = sdService;
    }

    @GetMapping
    public List<SD> getAll() {
        return sdService.findAll();
    }

    @PostMapping
    public SD create(@RequestBody SD sd) {
        return sdService.save(sd);
    }
}
