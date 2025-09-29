package com.youthcase.orderflow.sd.controller;

import com.youthcase.orderflow.sd.domain.SalesHeader;
import com.youthcase.orderflow.sd.domain.SalesItem;
import com.youthcase.orderflow.sd.service.SDService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sd")
public class SDController {

//    private final SDService sdService;
//
//    public SDController(SDService sdService) {
//        this.sdService = sdService;
//    }
//
//    @GetMapping
//    public List<SalesItem> getAll() {
//        return sdService.findAll();
//    }
//
//    @PostMapping
//    public SDSales create(@RequestBody SDSales sdSales) {
//        return sdService.save(sdSales);
//    }
}
