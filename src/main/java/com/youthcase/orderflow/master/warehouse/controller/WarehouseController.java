package com.youthcase.orderflow.master.warehouse.controller;

import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.dto.WarehouseRequestDTO;
import com.youthcase.orderflow.master.warehouse.dto.WarehouseResponseDTO;
import com.youthcase.orderflow.master.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    // ────────────────────────────────
    // 🔹 [ADMIN or ENVIRONMENT_EDIT] 창고 등록
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ENVIRONMENT_EDIT')")
    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> create(@RequestBody @Valid WarehouseRequestDTO dto) {
        Warehouse created = warehouseService.createWarehouse(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new WarehouseResponseDTO(created));
    }

    // ────────────────────────────────
    // 🔹 [ADMIN] 전체 창고 조회
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<WarehouseResponseDTO>> findAll() {
        List<WarehouseResponseDTO> response = warehouseService.getAllWarehouses()
                .stream()
                .map(WarehouseResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ────────────────────────────────
    // 🔹 [ADMIN or ENVIRONMENT_EDIT] 점포별 창고 조회
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ENVIRONMENT_EDIT')")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<WarehouseResponseDTO>> findByStore(@PathVariable String storeId) {
        List<WarehouseResponseDTO> response = warehouseService.getWarehousesByStoreId(storeId)
                .stream()
                .map(WarehouseResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ────────────────────────────────
    // 🔹 [ADMIN or ENVIRONMENT_EDIT] 창고 정보 수정
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ENVIRONMENT_EDIT')")
    @PutMapping("/{warehouseId}")
    public ResponseEntity<WarehouseResponseDTO> update(
            @PathVariable String warehouseId,
            @RequestBody @Valid WarehouseRequestDTO dto
    ) {
        Warehouse updated = warehouseService.updateWarehouse(warehouseId, dto);
        return ResponseEntity.ok(new WarehouseResponseDTO(updated));
    }

    // ────────────────────────────────
    // 🔹 [ADMIN or ENVIRONMENT_EDIT] 창고 삭제
    // ────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ENVIRONMENT_EDIT')")
    @DeleteMapping("/{warehouseId}")
    public ResponseEntity<Void> delete(@PathVariable String warehouseId) {
        warehouseService.deleteWarehouse(warehouseId);
        return ResponseEntity.noContent().build();
    }
}
