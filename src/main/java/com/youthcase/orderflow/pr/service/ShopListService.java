package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.dto.ShopListItemDto;

import com.youthcase.orderflow.master.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ShopListService {

    private final ProductRepository productRepository;

    // name > gtin > category 순으로 필터. 모두 없으면 전체.
    public Page<ShopListItemDto> list(String name, String gtin, String categoryCode,
                                      LocalDate orderDate, Pageable p) {
        LocalDate od = (orderDate != null) ? orderDate : LocalDate.now();

        var page = (name != null && !name.isBlank())
                ? productRepository.findByProductNameContainingIgnoreCase(name, p)
                : (gtin != null && !gtin.isBlank())
                ? productRepository.findByGtinContaining(gtin, p)
                : (categoryCode != null && !categoryCode.isBlank())
                ? productRepository.findByCategory_KanCode(categoryCode, p)
                : productRepository.findAll(p);

        return page.map(prod -> {
            var c = prod.getCategory();
            var due = od.plusDays(Math.max(0, prod.getStorageMethod().getLeadTimeDays()));
            return new ShopListItemDto(
                    prod.getGtin(), prod.getProductName(), prod.getUnit(), prod.getPrice(), prod.getStorageMethod(),
                    c!=null? c.getKanCode():null,
                    c!=null? c.getSmallCategory():null,
                    prod.getImageUrl(),
                    prod.getDescription(),
                    od, due
            );
        });
    }
}