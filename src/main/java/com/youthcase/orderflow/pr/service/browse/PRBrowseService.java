package com.youthcase.orderflow.pr.service.browse;

import com.youthcase.orderflow.pr.domain.enums.StorageMethod;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import com.youthcase.orderflow.pr.service.browse.dto.ProductItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PRBrowseService {
    private final ProductRepository productRepository;

    public record CornerDto(String id, String name, String desc, Integer categoryCount) {
    }

    public record CategoryNodeDto(String id, String name, Integer childrenCount) {
    }

    @Transactional(readOnly = true)
    public List<CornerDto> corners(String zone) {
        var sm = StorageMethod.fromInput(zone);
        String total = sm.getDisplayName();               // "실온/냉장/냉동/기타"
        boolean isOther = (sm == StorageMethod.OTHER);

        var rows = isOther
                ? productRepository.findCornersByTotalCategoryUsingLarge(total)   // 기타: 대분류 기준
                : productRepository.findCornersByTotalCategory(total);            // 나머지: 중분류 우선

        return rows.stream()
                .map(r -> {
                    String name = (String) r[0];   // ★ 이 줄이 빠져 있었음
                    if (name == null || name.isBlank()) name = "기타";
                    int cnt = ((Number) r[1]).intValue();
                    return new CornerDto(slug(name), name, "", cnt);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductItemDto> productsByKan(String kan, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategory_KanCode(kan, pageable)
                .stream()
                .map(p -> new ProductItemDto(
                        p.getGtin(),
                        p.getProductName(),
                        p.getUnit() != null ? p.getUnit().name() : null,
                        p.getPrice() != null ? p.getPrice().toPlainString() : null,
                        p.getImageUrl(),
                        Boolean.TRUE.equals(p.getOrderable())
                ))
                .toList();
    }

    /* ───────── 내부 헬퍼 ───────── */
    private String slug(String s) {
        if (s == null || s.isBlank()) return "기타";
        return s.replaceAll("\\s+", "_");
    }

    private String unslug(String s) {
        if (s == null || s.isBlank()) return null;
        return s.replace('_', ' ');
    }

    /**
     * 코너 선택 시 KAN 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<CategoryNodeDto> categories(String zone, String cornerIdOrName) {
        var sm = StorageMethod.fromInput(zone);
        String total = sm.getDisplayName();
        boolean isOther = (sm == StorageMethod.OTHER);

        String cornerName = unslug(cornerIdOrName);
        // ★ 추가: '기타'를 null로 변환(레포 쿼리의 NULL-safe 비교와 맞물림)
        if ("기타".equals(cornerName)) cornerName = null;

        var rows = isOther
                ? productRepository.findKanByTotalCategoryAndLargeCorner(total, cornerName) // 기타: 대분류로 매칭
                : productRepository.findKanByTotalCategoryAndCorner(total, cornerName);     // 나머지: 중분류 우선

        return rows.stream()
                .map(r -> new CategoryNodeDto(
                        (String) r[0],                     // id = KAN_CODE
                        (String) r[1],                     // name = 표시 라벨
                        ((Number) r[2]).intValue()         // childrenCount = 상품 수
                ))
                .toList();
    }
}


