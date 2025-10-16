package com.youthcase.orderflow.bi.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * BI_003 - 점포 운영환경 수정 요청 DTO
 * 관리자 및 ENVIRONMENT_EDIT 권한 소유자가 공통 사용
 */
@Getter @Setter
public class StoreConfigUpdateRequest {

    @Size(max = 50)
    private String ownerName; // 점장명 ✅

    @Size(max = 50)
    private String bizHours; // 영업시간 ✅

    @Size(max = 200)
    private String address; // 주소 ✅

    @Size(max = 200)
    private String addressDetail; // 상세주소 ✅

    @Size(max = 10)
    private String postCode; // 우편번호 ✅

    private Boolean active; // 운영여부 ✅

    @Size(max = 20)
    @Pattern(regexp = "^[0-9\\-+()]*$", message = "전화번호 형식이 올바르지 않습니다.")
    private String contactNumber; // 연락처 ✅
}
