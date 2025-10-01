package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.DTO.GRHeaderRequest;
import com.youthcase.orderflow.pr.domain.GRHeader;
import com.youthcase.orderflow.pr.service.GRHeaderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gr-headers")
@RequiredArgsConstructor
public class GRHeaderController {

    private final GRHeaderService grHeaderService;

    @GetMapping
    public List<GRHeader> getAllGRHeaders() {
        return grHeaderService.getAllGRHeaders();
    }

    @GetMapping("/{grId}")
    public GRHeader getGRHeader(@PathVariable String grId) {
        return grHeaderService.getGRHeaderById(grId)
                .orElseThrow(() -> new RuntimeException("GRHeader not found"));
    }

    @PostMapping
    public GRHeader createGRHeader(@Valid @RequestBody GRHeaderRequest request) {
        GRHeader grHeader = new GRHeader();
        grHeader.setStatus(request.getStatus());
        grHeader.setTotalAmount(request.getTotalAmount());
        grHeader.setRemarks(request.getRemarks());
        // POHeader, User 엔티티를 Service에서 조회 후 설정 필요
        return grHeaderService.saveGRHeader(grHeader);
    }

    @PutMapping("/{grId}")
    public GRHeader updateGRHeader(@PathVariable String grId, @Valid @RequestBody GRHeaderRequest request) {
        GRHeader grHeader = grHeaderService.getGRHeaderById(grId)
                .orElseThrow(() -> new RuntimeException("GRHeader not found"));

        grHeader.setStatus(request.getStatus());
        grHeader.setTotalAmount(request.getTotalAmount());
        grHeader.setRemarks(request.getRemarks());
        // POHeader, User 엔티티 갱신 필요
        return grHeaderService.saveGRHeader(grHeader);
    }

    @DeleteMapping("/{grId}")
    public void deleteGRHeader(@PathVariable String grId) {
        grHeaderService.deleteGRHeader(grId);
    }
}