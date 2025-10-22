package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.pr.dto.PRRecommendDto;
import com.youthcase.orderflow.pr.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pr/stores/{storeId}")
public class RecommendController {

    private final RecommendService recommendService; // 생성자 주입

    @GetMapping("/recommendations")
    public ResponseEntity<PRRecommendDto> recommend(
            @PathVariable String storeId,
            @RequestParam(required = false) String categories,        // "음료,스낵"
            @RequestParam(required = false) String zone,              // room|chilled|frozen|other
            @RequestParam(required = false) List<String> zones,
            @RequestParam(required = false, defaultValue = "3") Integer limitPerCategory
    ) {
        // 1) categories 파싱 (비었으면 전체 허용)
        List<String> catList = parseCsv(categories);

        // 2) zones 파싱 (우선순위: zones > zone). 비었으면 전체 허용
        List<StorageMethod> zoneList = parseZones(zones, zone);

        // 3) limit 안정화(가드)
        int limit = clamp(limitPerCategory, 1, 50);

        // 4) 서비스 호출 (서비스 시그니처를 List<StorageMethod>로 변경)
        PRRecommendDto body = recommendService.recommend(storeId, catList, zoneList, limit);
        return ResponseEntity.ok(body);
    }

    private static List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static List<StorageMethod> parseZones(List<String> zones, String zone) {
        // zones 파라미터가 있으면(반복 파라미터 or "a,b" 자동 분해) 그걸 우선 사용
        if (zones != null && !zones.isEmpty()) {
            return zones.stream()
                    .flatMap(s -> Arrays.stream(s.split(","))) // "room,chilled" 한 번 더 안전 분해
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(StorageMethod::fromInput)   // "room"/"ROOM_TEMP" 등 모두 허용하게 fromInput 구현
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
        }
        // 없으면 단일 zone 사용(하위호환)
        if (zone != null && !zone.isBlank()) {
            return List.of(StorageMethod.fromInput(zone.trim()));
        }
        // 아무 것도 없으면 전체 허용 의미로 빈 리스트 반환(서비스에서 해석)
        return List.of();
    }

    private static int clamp(Integer v, int min, int max) {
        if (v == null) return min;
        return Math.max(min, Math.min(max, v));
    }
}
