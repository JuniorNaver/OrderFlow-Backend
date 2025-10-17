package com.youthcase.orderflow.master.store.dto;

import com.youthcase.orderflow.master.store.domain.Store;
import lombok.Builder;
import lombok.Getter;

/**
 * BI_003 - 점포 운영환경 조회/수정 응답 DTO
 * 관리자 및 ENVIRONMENT_EDIT 권한 소유자가 공통으로 사용
 */
@Getter
@Builder
public class StoreConfigResponseDTO {
    private String storeId;
    private String storeName;
    private String brandCode;
    private String ownerName;
    private String bizHours;
    private String address;
    private String addressDetail;
    private String postCode;
    private String contactNumber;
    private Boolean active;

    public static StoreConfigResponseDTO fromEntity(Store store) {
        return StoreConfigResponseDTO.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .brandCode(store.getBrandCode())
                .ownerName(store.getOwnerName())
                .bizHours(store.getBizHours())
                .address(store.getAddress())
                .addressDetail(store.getAddressDetail())
                .postCode(store.getPostCode())
                .contactNumber(store.getContactNumber())
                .active(store.getActive())
                .build();
    }
}
