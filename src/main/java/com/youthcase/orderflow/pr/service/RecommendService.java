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
                                  StorageMethod zoneFilter,
                                  Integer limitPerCategory) {

        final int limit = (limitPerCategory == null || limitPerCategory <= 0) ? 3 : limitPerCategory;

        // DB 필터로 후보 추출
        List<Product> candidates = productRepository.findRecommendable(categories, zoneFilter);

        // 카테고리별 상한 적용
        Map<String, Integer> bucket = new HashMap<>();
        List<PRRecommendItemDto> items = new ArrayList<>();

        for (Product p : candidates) {
            String catName = resolveCategory(p);
            if (categories != null && !categories.isEmpty() && !categories.contains(catName)) continue;

            int used = bucket.getOrDefault(catName, 0);
            if (used >= limit) continue;

            items.add(toItem(p, catName));
            bucket.put(catName, used + 1);
        }

        return new PRRecommendDto(storeId, OffsetDateTime.now().toString(), items);
    }

    private static String resolveCategory(Product p) {
        if (p.getCategory() == null) return "기타";
        var c = p.getCategory();
        if (notBlank(c.getSmallCategory()))  return c.getSmallCategory();
        if (notBlank(c.getMediumCategory())) return c.getMediumCategory();
        if (notBlank(c.getLargeCategory()))  return c.getLargeCategory();
        return "기타";
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
