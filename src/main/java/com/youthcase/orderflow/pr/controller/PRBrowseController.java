package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.service.InventoryService;
import com.youthcase.orderflow.pr.service.browse.PRBrowseService;
import com.youthcase.orderflow.pr.service.browse.dto.ProductItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pr")
@RequiredArgsConstructor
@Validated
public class PRBrowseController {

    private final PRBrowseService svc;
    private final InventoryService inv;

    /**
     * 존별 코너 목록 (실온/냉장/냉동/기타)
     * GET /api/v1/pr/browse/corners?zone=room|chilled|frozen|other
     */
    @GetMapping("/corners")
    public ResponseEntity<List<PRBrowseService.CornerDto>> corners(
            @RequestParam String zone
    ) {
        var body = svc.corners(zone);
        return ResponseEntity.ok(body);
    }

    /**
     * 코너 선택 시 KAN 카테고리 목록
     * GET /api/v1/pr/browse/categories?zone=...&cornerId=...
     *  - cornerId는 프론트에서 slug로 전달 (예: "라면_코너"), 서비스에서 unslug 처리
     */
    @GetMapping("/categories")
    public ResponseEntity<List<PRBrowseService.CategoryNodeDto>> categories(
            @RequestParam String zone,
            @RequestParam("cornerId") String cornerId
    ) {
        var body = svc.categories(zone, cornerId);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductItemDto>> products(
            @RequestParam String kan,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        int p = Math.max(0, page);
        int s = Math.min(Math.max(1, size), 100);

        var body = svc.productsByKan(kan, p, s);
        return ResponseEntity.ok(body);
    }

    // 인벤토리 쪽
    //조회

    record AvailDto(String gtin, Long available) {}
    @GetMapping("/inventory")
    public AvailDto available(@RequestParam String gtin){
        return new AvailDto(gtin, inv.getAvailable(gtin));
    }

    record QtyReq(String gtin, @Min(1) Long qty) {}

    @PostMapping("/inventory/reserve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserve(@RequestBody @Valid QtyReq req){ inv.reserve(req.gtin(), req.qty()); }

    @PostMapping("/inventory/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(@RequestBody @Valid QtyReq req){ inv.release(req.gtin(), req.qty()); }

    @PostMapping("/inventory/commit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void commit(@RequestBody @Valid QtyReq req){ inv.commit(req.gtin(), req.qty()); }

    /* ───────── 에러 핸들링(간단) ───────── */

    // 서비스의 validateZone() 등에서 IllegalArgumentException 던지면 400으로 변환
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException e) {
        return e.getMessage();
    }

    // 예외
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConflict(RuntimeException e){ return e.getMessage(); }
}
