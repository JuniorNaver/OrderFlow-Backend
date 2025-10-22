package com.youthcase.orderflow.pr.dto;

import com.youthcase.orderflow.master.product.domain.StorageMethod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PRRecommendRequestDto (
        List<String> categories,                 // "BEV","SNACK","RTE" 처럼 '코드'로 통일 권장
        List<StorageMethod> zones,               // ROOM_TEMP, COLD, FROZEN, OTHER (복수 지원)
        @Positive @Max(50) Integer limitPerCategory // 카테고리당 상한
) {}

