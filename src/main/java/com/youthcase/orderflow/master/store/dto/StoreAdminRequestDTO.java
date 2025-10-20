package com.youthcase.orderflow.master.store.dto;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.domain.StoreType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StoreAdminRequestDTO {

    // ────────────────
    // 기준정보
    // ────────────────
    @NotBlank
    @Size(max = 10)
    private String storeId;

    @NotBlank
    @Size(max = 100)
    private String storeName;

    @NotBlank
    @Size(max = 10)
    private String brandCode;

    @NotBlank
    @Size(max = 10)
    private String regionCode;

    @NotNull
    private StoreType storeType; // ENUM (DIRECT, FRANCHISE 등)

    private LocalDate openDate;
    private String managerId;

    // ────────────────
    // 위치정보
    // ────────────────
    @NotBlank
    @Size(max = 200)
    private String address;

    @Size(max = 200)
    private String addressDetail;

    @Size(max = 10)
    private String postCode;

    // ────────────────
    // 운영정보
    // ────────────────
    @Size(max = 50)
    private String ownerName;

    @Size(max = 50)
    private String bizHours;

    @Pattern(regexp = "^[0-9\\-+()]*$", message = "전화번호 형식이 올바르지 않습니다.")
    private String contactNumber;

    @Size(max = 1)
    private Boolean active;

    public Store toEntity() {
        return Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .brandCode(brandCode)
                .regionCode(regionCode)
                .storeType(storeType)
                .openDate(openDate)
                .managerId(managerId)
                .address(address)
                .addressDetail(addressDetail)
                .postCode(postCode)
                .ownerName(ownerName)
                .bizHours(bizHours)
                .contactNumber(contactNumber)
                .active(active) // ✅ 관리자가 토글로 설정 가능
                .build();
    }

}
