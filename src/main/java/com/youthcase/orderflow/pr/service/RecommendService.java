package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.pr.dto.PRRecommendDto;
import com.youthcase.orderflow.pr.dto.PRRecommendItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class RecommendService {

    private final ProductRepository productRepository;

    public PRRecommendDto recommend(String storeId,
                                  List<String> categories,
                                  List<StorageMethod> zones,
                                  Integer limitPerCategory) {

        final int limit = (limitPerCategory == null || limitPerCategory <= 0) ? 3 : limitPerCategory;
        final List<String> catList = (categories == null) ? List.of() : categories;
        final List<StorageMethod> zoneList = (zones == null) ? List.of() : zones;

        // DB 필터로 후보 추출
        List<Product> candidates = productRepository.findRecommendable(List.of(), zoneList);

        // 카테고리별 상한 적용
        Map<String, Integer> bucket = new HashMap<>();
        List<PRRecommendItemDto> items = new ArrayList<>();

        for (Product p : candidates) {
            String catName = mapToGroup(p);
            if (categories != null && !categories.isEmpty() && !categories.contains(catName)) continue;

            int used = bucket.getOrDefault(catName, 0);
            if (used >= limit) continue;

            items.add(toItem(p, catName));
            bucket.put(catName, used + 1);
        }

        return new PRRecommendDto(storeId, OffsetDateTime.now().toString(), items);
    }

    private static String mapToGroup(Product p) {
        String small = p.getCategory() != null ? p.getCategory().getSmallCategory() : null;
        String medium = p.getCategory() != null ? p.getCategory().getMediumCategory() : null;
        String large = p.getCategory() != null ? p.getCategory().getLargeCategory() : null;
        String v = firstNotBlank(small, medium, large);

        if (v == null) return "기타";
        if (v.contains("음료")) return "음료"; // 냉장음료 등 모두 포함

        // 스낵 계열
        if (List.of("스낵","파이류","쿠키류","젤리류","사탕","껌류","초콜릿류","시리얼").contains(v)) return "스낵";

        // 즉석식품 계열
        if (v.startsWith("즉석") || (medium != null && medium.contains("즉석식품"))) return "즉석식품";

        return "기타";
    }
    private static String firstNotBlank(String... xs){
        for (String x: xs) if (x!=null && !x.isBlank()) return x;
        return null;
    }
    private static boolean notBlank(String s){ return s != null && !s.isBlank(); }

    private static PRRecommendItemDto toItem(Product p, String categoryName) {
        return new PRRecommendItemDto(
                p.getGtin(),
                p.getProductName(),
                1, // 기본 제안 수량 (BI 연동 전)
                p.getPrice() != null ? p.getPrice().intValue() : null,
                p.getStorageMethod(),
                categoryName,
                null, // reason
                p.getUnit() != null ? p.getUnit().name() : null,
                p.getImageUrl()
        );
    }
}
