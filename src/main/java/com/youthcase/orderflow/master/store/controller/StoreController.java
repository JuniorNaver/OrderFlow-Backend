package com.youthcase.orderflow.master.store.controller;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.dto.StoreAdminRequestDTO;
import com.youthcase.orderflow.master.store.dto.StoreEnvRequestDTO;
import com.youthcase.orderflow.master.store.dto.StoreResponseDTO;
import com.youthcase.orderflow.master.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // ────────────────────────────────
    // 🔹 [ADMIN] 지점 등록
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public StoreResponseDTO create(@RequestBody StoreAdminRequestDTO dto) {
        Store store = storeService.createByAdmin(dto);
        return StoreResponseDTO.fromEntity(store);
    }

    // ────────────────────────────────
    // 🔹 [ADMIN] 지점 전체 조회
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<StoreResponseDTO> findAll() {
        return storeService.findAll().stream()
                .map(StoreResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ────────────────────────────────
    // 🔹 [ADMIN] 지점 단일 조회
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{storeId}")
    public StoreResponseDTO findById(@PathVariable String storeId) {
        return StoreResponseDTO.fromEntity(storeService.findById(storeId));
    }

    // ────────────────────────────────
    // 🔹 [ADMIN] 지점 전체 수정
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{storeId}")
    public StoreResponseDTO updateByAdmin(
            @PathVariable String storeId,
            @RequestBody StoreAdminRequestDTO dto
    ) {
        return StoreResponseDTO.fromEntity(storeService.updateByAdmin(storeId, dto));
    }

    // ────────────────────────────────
    // 🔹 [ADMIN] 지점 삭제
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{storeId}")
    public void delete(@PathVariable String storeId) {
        storeService.delete(storeId);
    }

    // ────────────────────────────────
    // 🔹 [ADMIN or ENVIRONMENT_EDIT] 점포 환경 조회
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ENVIRONMENT_EDIT')")
    @GetMapping("/{storeId}/env")
    public StoreResponseDTO getStoreEnv(@PathVariable String storeId) {
        return StoreResponseDTO.fromEntity(storeService.findById(storeId));
    }

    // ────────────────────────────────
    // 🔹 [ADMIN or ENVIRONMENT_EDIT] 점포 운영환경, 위치 수정
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ENVIRONMENT_EDIT')")
    @PutMapping("/{storeId}/env")
    public StoreResponseDTO updateStoreEnv(
            @PathVariable String storeId,
            @RequestBody StoreEnvRequestDTO dto
    ) {
        return StoreResponseDTO.fromEntity(storeService.updateEnv(storeId, dto));
    }
}
