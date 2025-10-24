package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pr")
@RequiredArgsConstructor
@Validated
public class InventoryController {
    private final InventoryService inv;

    //조회
    record AvailDto(String gtin, Long available) {}
    @GetMapping("/inventory")
    public AvailDto available(@RequestParam String gtin){
        return new AvailDto(gtin, inv.getAvailable(gtin));
    }

    record QtyReq(String gtin, @Min(1) int qty) {}

    @PostMapping("/inventory/reserve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserve(@RequestBody @Valid QtyReq req){ inv.reserve(req.gtin(), req.qty()); }

    @PostMapping("/inventory/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(@RequestBody @Valid QtyReq req){ inv.release(req.gtin(), req.qty()); }

    @PostMapping("/inventory/commit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void commit(@RequestBody @Valid QtyReq req){ inv.commit(req.gtin(), req.qty()); }
// 예외

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConflict(RuntimeException e){ return e.getMessage(); }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(RuntimeException e){ return e.getMessage(); }
}
