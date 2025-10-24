package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile({"dev","local"})
@RestController
@RequestMapping("/debug/inventory")
@RequiredArgsConstructor
public class InventoryDebugController {

    private final InventoryService inventoryService;

    @PostMapping("/receive")
    public String receive(@RequestParam String gtin, @RequestParam int qty) {
        inventoryService.receive(gtin, qty);
        return "OK (receive) gtin=" + gtin + ", qty=" + qty;
    }

    @PostMapping("/reserve")
    public String reserve(@RequestParam String gtin, @RequestParam int qty) {
        inventoryService.reserve(gtin, qty);
        return "OK (reserve) gtin=" + gtin + ", qty=" + qty;
    }

    @PostMapping("/release")
    public String release(@RequestParam String gtin, @RequestParam int qty) {
        inventoryService.release(gtin, qty);
        return "OK (release) gtin=" + gtin + ", qty=" + qty;
    }

    @PostMapping("/commit")
    public String commit(@RequestParam String gtin, @RequestParam int qty) {
        inventoryService.commit(gtin, qty);
        return "OK (commit) gtin=" + gtin + ", qty=" + qty;
    }

    @GetMapping("/available")
    public Long available(@RequestParam String gtin) {
        return inventoryService.getAvailable(gtin);
    }
}
