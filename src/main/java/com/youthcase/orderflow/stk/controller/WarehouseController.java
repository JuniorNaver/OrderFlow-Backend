package com.youthcase.orderflow.stk.controller;

import com.youthcase.orderflow.stk.domain.Warehouse;
import com.youthcase.orderflow.stk.service.WarehouseService;
import com.youthcase.orderflow.stk.dto.WarehouseRequestDTO;
import com.youthcase.orderflow.stk.dto.WarehouseResponseDTO;
import com.youthcase.orderflow.stk.dto.WarehouseUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // 유효성 검증을 위해 추가

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;


    // 1. 창고 등록 (Create) - RequestDto 사용, @Valid 적용
    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> createWarehouse(@RequestBody @Valid WarehouseRequestDTO requestDto) {
        // DTO를 Entity로 변환하여 Service에 전달
        Warehouse newWarehouse = requestDto.toEntity();

        Warehouse createdWarehouse = warehouseService.createWarehouse(newWarehouse);

        // Service에서 받은 Entity를 ResponseDto로 변환하여 반환
        return new ResponseEntity<>(new WarehouseResponseDTO(createdWarehouse), HttpStatus.CREATED); // 201
    }

    // 2. 전체 창고 조회 (Read All) - ResponseDto 사용
    @GetMapping
    public ResponseEntity<List<WarehouseResponseDTO>> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();

        // Entity List를 DTO List로 변환
        List<WarehouseResponseDTO> response = warehouses.stream()
                .map(WarehouseResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response); // 200 OK
    }

    // 3. 특정 창고 조회 (Read One) - ResponseDto 사용
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> getWarehouseById(@PathVariable("id") String id) {
        try {
            Warehouse warehouse = warehouseService.getWarehouseById(id);
            // Entity를 ResponseDto로 변환
            return ResponseEntity.ok(new WarehouseResponseDTO(warehouse)); // 200 OK
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 4. 창고 정보 수정 (Update) - UpdateDto 사용
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouse(@PathVariable("id") String id, @RequestBody @Valid WarehouseUpdateDTO updateDto) {
        try {
            // Service는 Entity를 받도록 그대로 두고, Controller에서 DTO의 필드만 전달
            Warehouse updatedWarehouse = warehouseService.updateWarehouse(id, updateDto);

            // Entity를 ResponseDto로 변환하여 반환
            return ResponseEntity.ok(new WarehouseResponseDTO(updatedWarehouse)); // 200 OK
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 5. 창고 삭제 (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable("id") String id){
        try {
            warehouseService.deleteWarehouse(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}