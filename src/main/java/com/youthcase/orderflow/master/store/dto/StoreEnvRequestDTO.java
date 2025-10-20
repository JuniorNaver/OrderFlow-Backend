package com.youthcase.orderflow.master.store.dto;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.domain.StoreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StoreEnvRequestDTO {

    // ────────────────
    // 위치정보 (점장 수정 가능)
    // ────────────────
    @NotBlank
    @Size(max = 200)
    private String address;

    @Size(max = 200)
    private String addressDetail;

    @Size(max = 10)
    private String postCode;

    // ────────────────
    // 운영정보 (점장 초기 수정 가능)
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
