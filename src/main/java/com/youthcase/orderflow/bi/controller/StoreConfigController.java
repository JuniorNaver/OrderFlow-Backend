package com.youthcase.orderflow.bi.controller;

import com.youthcase.orderflow.bi.dto.StoreConfigResponse;
import com.youthcase.orderflow.bi.dto.StoreConfigUpdateRequest;
import com.youthcase.orderflow.bi.service.environment.StoreConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings/store")
@RequiredArgsConstructor
public class StoreConfigController {

    private final StoreConfigService storeConfigService;

    /**
     * BI_003 - 점포 운영환경 조회
     * 관리자(ROLE_ADMIN) 또는 ENVIRONMENT_EDIT 권한을 가진 사용자만 가능
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ENVIRONMENT_EDIT')")
    @GetMapping("/{storeId}")
    public StoreConfigResponse getStoreConfig(@PathVariable String storeId) {
        return StoreConfigResponse.fromEntity(storeConfigService.getStoreConfig(storeId));
    }

    /**
     * BI_003 - 점포 운영환경 수정
     * 관리자(ROLE_ADMIN) 또는 ENVIRONMENT_EDIT 권한을 가진 사용자만 가능
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ENVIRONMENT_EDIT')")
    @PutMapping("/{storeId}")
    public StoreConfigResponse updateStoreConfig(
            @PathVariable String storeId,
            @RequestBody StoreConfigUpdateRequest dto) {
        return StoreConfigResponse.fromEntity(
                storeConfigService.updateStoreConfig(storeId, dto)
        );
    }
}
