package com.youthcase.orderflow.master.store.controller;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService service;

    /**
     * 1️⃣ 지점 등록 (관리자 전용)
     * 최초 등록 시에만 사용 — 이미 등록된 ID는 예외 발생
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/init")
    public Store create(@RequestBody Store request) {
        return service.create(request);
    }

    /**
     * 2️⃣ 지점 전체 조회 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Store> findAll() {
        return service.findAll();
    }

    /**
     * 3️⃣ 지점 단일 조회 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{storeId}")
    public Store findById(@PathVariable String storeId) {
        return service.findById(storeId);
    }

    /**
     * 4️⃣ 지점 삭제 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{storeId}")
    public void delete(@PathVariable String storeId) {
        service.delete(storeId);
    }
}
