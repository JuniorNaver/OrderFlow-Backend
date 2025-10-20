package com.youthcase.orderflow.master.store.dto;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.domain.StoreType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class StoreResponseDTO {

    private String storeId;
    private String storeName;
    private String brandCode;
    private String regionCode;
    private StoreType storeType; // ENUM (DIRECT, FRANCHISE 등)
    private LocalDate openDate;
    private String managerId;

    // ────────────────
    // 위치정보
    // ────────────────
    private String address;
    private String addressDetail;
    private String postCode;

    // ────────────────
    // 운영정보
    // ────────────────
    private String ownerName;
    private String bizHours;
    private String contactNumber;
    private Boolean active;

    // ────────────────────────────────
    // 🔹 시스템 관리 컬럼 (자동)
    // ────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal longitude;
    private BigDecimal latitude;

    // ✅ Entity → DTO 변환
    public static StoreResponseDTO fromEntity(Store store) {
        StoreResponseDTO dto = new StoreResponseDTO();
        dto.setStoreId(store.getStoreId());
        dto.setStoreName(store.getStoreName());
        dto.setBrandCode(store.getBrandCode());
        dto.setRegionCode(store.getRegionCode());
        dto.setStoreType(store.getStoreType());
        dto.setOpenDate(store.getOpenDate());
        dto.setManagerId(store.getManagerId());
        dto.setAddress(store.getAddress());
        dto.setAddressDetail(store.getAddressDetail());
        dto.setPostCode(store.getPostCode());
        dto.setOwnerName(store.getOwnerName());
        dto.setBizHours(store.getBizHours());
        dto.setContactNumber(store.getContactNumber());
        dto.setActive(store.getActive());
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());
        dto.setLatitude(store.getLatitude());
        dto.setLongitude(store.getLongitude());
        return dto;
    }
}
