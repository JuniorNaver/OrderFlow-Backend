package com.youthcase.orderflow.master.controller;

import com.youthcase.orderflow.master.dto.ProductCreateDto;
import com.youthcase.orderflow.master.dto.ProductResponseDto;
import com.youthcase.orderflow.master.dto.ProductUpdateDto;
import com.youthcase.orderflow.master.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** 목록/검색
     * GET /api/products?name=콜라&gtin=8801&category=BEV&page=0&size=20&sort=productName,asc
     * 200 OK + Page<ProductResponseDto>
     */
    @GetMapping
    public Page<ProductResponseDto> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gtin,
            @RequestParam(required = false, name = "category") String categoryCode,
            @PageableDefault(size = 20, sort = "productName") Pageable pageable) {

        // 서비스에서 분기: name > gtin > category > all (네가 원하면 우선순위 조정)
        return productService.listAdvanced(name, gtin, categoryCode, pageable);
    }

    /** 단건 조회
     * GET /api/products/{gtin}
     * 200 OK or 404
     */
    @GetMapping("/{gtin}")
    public ProductResponseDto get(@PathVariable String gtin) {
        return productService.get(gtin);
    }

    /** 생성
     * POST /api/products
     * 201 Created + Location 헤더
     * 400(검증 실패), 409(중복 GTIN)
     */
    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductCreateDto dto) {
        var resp = productService.create(dto);
        return ResponseEntity
                .created(URI.create("/api/products/" + resp.gtin()))
                .body(resp);
    }

    /** 수정
     * PUT /api/products/{gtin}
     * 200 OK or 404
     */
    @PutMapping("/{gtin}")
    public ProductResponseDto update(@PathVariable String gtin,
                                     @Valid @RequestBody ProductUpdateDto dto) {
        return productService.update(gtin, dto);
    }

    /** 삭제
     * DELETE /api/products/{gtin}
     * 204 No Content (멱등)
     */
    @DeleteMapping("/{gtin}")
    public ResponseEntity<Void> delete(@PathVariable String gtin) {
        productService.delete(gtin);
        return ResponseEntity.noContent().build();
    }
}