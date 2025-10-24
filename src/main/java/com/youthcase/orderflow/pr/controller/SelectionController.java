package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.dto.SelectionItemDto;
import com.youthcase.orderflow.pr.service.SelectionForwardService;
import com.youthcase.orderflow.pr.service.SelectionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/pr/selection")
@RequiredArgsConstructor
public class SelectionController {
    private final SelectionService selection;           // 임시 담기 (옵션)
    private final SelectionForwardService forward;      // PO 포워딩

    private String owner(HttpServletRequest req) {
        var h = req.getHeader("X-Owner-Id");
        return (h == null || h.isBlank()) ? "guest" : h;
    }

    @PostMapping("/items")           // 담기(임시 저장)
    public ResponseEntity<Void> add(@RequestBody @Valid SelectionItemDto req, HttpServletRequest http) {
        selection.add(owner(http), req);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/items")            // 임시 목록
    public List<SelectionItemDto> list(HttpServletRequest http) {
        return selection.list(owner(http));
    }

    @PatchMapping("/items/{gtin}")   // 수량 변경
    public ResponseEntity<Void> change(@PathVariable String gtin, @RequestParam Long qty, HttpServletRequest http) {
        selection.changeQty(owner(http), gtin, qty);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{gtin}")  // 제거
    public ResponseEntity<Void> remove(@PathVariable String gtin, HttpServletRequest http) {
        selection.remove(owner(http), gtin);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")        // 장바구니로 전송(PO) + 비우기
    public ResponseEntity<Void> checkout(HttpServletRequest http) {
        var ownerId = owner(http);
        forward.checkout(ownerId, selection.list(ownerId));
        selection.clear(ownerId);
        return ResponseEntity.accepted().build();
    }
}