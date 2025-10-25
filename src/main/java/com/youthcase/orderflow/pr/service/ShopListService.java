package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.pr.domain.ShopList;
import com.youthcase.orderflow.pr.dto.ShopListItemDto;

import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.pr.repository.ShopListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class  ShopListService {

    private final ShopListRepository shopListRepository;
    private final ProductRepository productRepository;

    /* 목록 */
    @Transactional(readOnly = true)
    public Page<ShopListItemDto> list(Pageable pageable, LocalDate orderDate) {
        return shopListRepository.findAll(pageable)
                .map(sl -> toItem(sl, orderDate));
    }

    /* 검색 */
    @Transactional(readOnly = true)
    public Page<ShopListItemDto> list(String name, String gtin, String categoryCode,
                                      LocalDate orderDate, Pageable pageable) {
        return shopListRepository.findByFilters(name, gtin, categoryCode, pageable)
                .map(sl -> toItem(sl, orderDate));
    }

    /* 단건 조회 */
    @Transactional(readOnly = true)
    public ShopListItemDto getById(Long id, LocalDate orderDate) {
        ShopList sl = shopListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ShopList not found: " + id));
        return toItem(sl, orderDate);
    }

    /* GTIN 기준 단건 조회 */
    @Transactional(readOnly = true)
    public ShopListItemDto getByGtin(String gtin, LocalDate orderDate) {
        ShopList sl = shopListRepository.findByProduct_Gtin(gtin)
                .orElseThrow(() -> new IllegalArgumentException("ShopList not found for GTIN: " + gtin));
        return toItem(sl, orderDate);
    }

    /* 생성 */
    public ShopListItemDto create(String gtin, String description, Boolean orderable) {
        Product product = productRepository.findById(gtin)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + gtin));

        if (shopListRepository.existsByProduct_Gtin(gtin)) {
            throw new IllegalStateException("ShopList already exists for GTIN: " + gtin);
        }

        ShopList sl = new ShopList();
        sl.setProduct(product);
        sl.setDescription(description);
        sl.setOrderable(orderable != null ? orderable : Boolean.TRUE);
        // 명시 스냅샷 주입(안 해도 @PrePersist에서 채워짐)
        sl.setPurchasePrice(product.getPrice());

        sl = shopListRepository.save(sl);
        return toItem(sl, null);
    }

    /* 수정 */
    public ShopListItemDto update(Long id, String description, Boolean orderable) {
        ShopList sl = shopListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ShopList not found: " + id));
        if (description != null) sl.setDescription(description);
        if (orderable != null)  sl.setOrderable(orderable);
        // 스냅샷은 과거 보존이 목적이라 수정하지 않음
        return toItem(sl, null);
    }

    /* 발주가능 토글 */
    public int updateOrderableByGtin(String gtin, boolean orderable) {
        return shopListRepository.updateOrderableByGtin(gtin, orderable);
    }

    /* 삭제 */
    public void delete(Long id) {
        if (!shopListRepository.existsById(id)) return;
        shopListRepository.deleteById(id);
    }

    /* 기간 필터 */
    @Transactional(readOnly = true)
    public Page<ShopListItemDto> listByCreatedBetween(Instant from, Instant to, Pageable pageable, LocalDate orderDate) {
        return shopListRepository.findByCreatedAtBetween(from, to, pageable)
                .map(sl -> toItem(sl, orderDate));
    }

    /* 통계 */
    @Transactional(readOnly = true)
    public PriceStatsDto purchasePriceStats() {
        var stats = shopListRepository.purchasePriceStats();
        return new PriceStatsDto(
                stats.getMin() != null ? stats.getMin() : BigDecimal.ZERO,
                stats.getAvg() != null ? stats.getAvg() : BigDecimal.ZERO,
                stats.getMax() != null ? stats.getMax() : BigDecimal.ZERO
        );
    }

    /* 리스트 프로젝션 그대로 반환(가벼운 목록이 필요할 때) */
    @Transactional(readOnly = true)
    public Page<ShopListRepository.Row> listRows(String q, Pageable pageable) {
        return shopListRepository.findListRows(q, pageable);
    }

    /* ---- 내부 도우미 ---- */
    private ShopListItemDto toItem(ShopList sl, LocalDate orderDate) {
        var p = sl.getProduct();
        var c = p.getCategory();
        var expected = (orderDate != null)
                ? orderDate.plusDays(p.getStorageMethod().getLeadTimeDays())
                : null;

        return new ShopListItemDto(
                sl.getId(),
                p.getGtin(),
                p.getProductName(),
                p.getUnit(),
                sl.getPurchasePrice(),
                p.getStorageMethod(),
                c != null ? c.getKanCode() : null,
                c != null ? c.getLargeCategory() : null, // 필요 시 medium/small로 교체
                p.getImageUrl(),
                sl.getDescription(),
                sl.getCreatedAt(),
                sl.getUpdatedAt(),
                orderDate,
                expected
        );
    }

    public record PriceStatsDto(BigDecimal min, BigDecimal avg, BigDecimal max) {}
}