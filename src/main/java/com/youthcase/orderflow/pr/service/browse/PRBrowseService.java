package com.youthcase.orderflow.pr.service.browse;

import com.youthcase.orderflow.pr.domain.StorageMethod;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PRBrowseService {
    private final ProductRepository productRepository;

    public record CornerDto(String id, String name, String desc, Integer categoryCount) {}
    public record CategoryNodeDto(String id, String name, Integer childrenCount) {}

    @Transactional(readOnly = true)
    public List<CornerDto> corners(String zone) {
        validateZone(zone);
        StorageMethod sm = toSM(zone);

        var rows = isOther(zone)
                ? productRepository.findCornersByZoneUsingLarge(sm)   // 기타: 대분류 기준
                : productRepository.findCornersByZone(sm);            // 나머지: 중분류 우선

        return rows.stream()
                .map(r -> {
                    String name = (String) r[0];   // ★ 이 줄이 빠져 있었음
                    if (name == null || name.isBlank()) name = "기타";
                    int cnt = ((Number) r[1]).intValue();
                    return new CornerDto(slug(name), name, "", cnt);
                })
                .toList();
    }

    private String slug(String s){
        if (s == null || s.isBlank()) return "기타";
        return s.replaceAll("\\s+","_");
    }
    private String unslug(String s){
        if (s == null || s.isBlank()) return null;
        return s.replace('_',' ');
    }
    private void validateZone(String zone){
        if (zone == null) throw new IllegalArgumentException("zone is required");
        switch (zone) {
            case "room","chilled","frozen","other" -> {}
            default -> throw new IllegalArgumentException("zone must be one of room|chilled|frozen|other");
        }
    }

    /** 코너 선택 시 KAN 카테고리 목록 */
    @Transactional(readOnly = true)
    public List<CategoryNodeDto> categories(String zone, String cornerIdOrName) {
        validateZone(zone);
        StorageMethod sm = toSM(zone);
        String cornerName = unslug(cornerIdOrName);

        // ★ 추가: '기타'를 null로 변환(레포 쿼리의 NULL-safe 비교와 맞물림)
        if ("기타".equals(cornerName)) cornerName = null;

        var rows = isOther(zone)
                ? productRepository.findKanByZoneAndLargeCorner(sm, cornerName) // 기타: 대분류로 매칭
                : productRepository.findKanByZoneAndCorner(sm, cornerName);     // 나머지: 중분류 우선

        return rows.stream()
                .map(r -> new CategoryNodeDto(
                        (String) r[0],                    // kan
                        (String) r[1],                    // name
                        ((Number) r[2]).intValue()        // productCount
                ))
                .toList();
    }

    /* ───────── 내부 헬퍼 ───────── */

    private boolean isOther(String zone) {
        return zone != null && (zone.equalsIgnoreCase("other") || zone.equals("기타"));
    }

    private StorageMethod toSM(String z){
        return switch (z) {
            case "room"    -> StorageMethod.ROOM_TEMP;
            case "chilled" -> StorageMethod.COLD;
            case "frozen"  -> StorageMethod.FROZEN;
            case "other"   -> null; // 기타는 스토리지 필터 해제
            default        -> null;
        };
    }
}