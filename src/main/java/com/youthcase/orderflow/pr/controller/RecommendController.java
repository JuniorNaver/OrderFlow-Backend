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
            @RequestParam(required = false, defaultValue = "3") Integer limitPerCategory
    ) {
        // categories 파싱
        List<String> catList = (categories == null || categories.isBlank())
                ? List.of()
                : Arrays.stream(categories.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // zone → StorageMethod (검증 포함)
        StorageMethod sm = (zone == null || zone.isBlank()) ? null : StorageMethod.fromInput(zone);

        // 서비스 호출
        PRRecommendDto body = recommendService.recommend(storeId, catList, sm, limitPerCategory);

        return ResponseEntity.ok(body);
    }
}
