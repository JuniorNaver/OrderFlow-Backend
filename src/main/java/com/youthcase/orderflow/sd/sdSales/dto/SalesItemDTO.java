package com.youthcase.orderflow.sd.sdSales.dto;

import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import lombok.*;

import java.math.BigDecimal;

/**
 * 🧾 SalesItemDTO
 * - SalesItem 엔티티의 데이터를 프론트로 전달하는 DTO
 * - JPQL DTO Projection(new ...) 및 도메인 변환 둘 다 지원
 */
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class SalesItemDTO {

    private Long no;             // SalesItem.no
    private String gtin;         // Product.GTIN
    private String productName;  // 상품명
    private BigDecimal sdPrice;  // 단가
    private int salesQuantity;   // 수량
    private int stockQuantity;   // 표시용 재고
    private BigDecimal subtotal; // 소계 (단가 * 수량)

    // ✅ JPQL용 생성자 (Hibernate가 이걸 사용함)
    // SUM() 결과는 Long/Integer/BigDecimal 등으로 나올 수 있으므로 Number로 받음
    public SalesItemDTO(Long no, String gtin, String productName,
                        BigDecimal sdPrice, int salesQuantity,
                        Number stockQuantity, BigDecimal subtotal) {
        this.no = no; // Hibernate는 no 필드로 인식
        this.gtin = gtin;
        this.productName = productName;
        this.sdPrice = sdPrice;
        this.salesQuantity = salesQuantity;
        this.stockQuantity = stockQuantity != null ? stockQuantity.intValue() : 0;
        this.subtotal = subtotal;
    }

    // ✅ 도메인 → DTO 변환 (일반 서비스/컨트롤러에서 사용)
    public static SalesItemDTO from(SalesItem s) {
        if (s == null) return null;

        String name = (s.getProduct() != null && s.getProduct().getProductName() != null)
                ? s.getProduct().getProductName()
                : "상품명 미등록";

        String gtin = (s.getProduct() != null && s.getProduct().getGtin() != null)
                ? s.getProduct().getGtin()
                : "UNKNOWN";

        BigDecimal price = (s.getSdPrice() != null) ? s.getSdPrice() : BigDecimal.ZERO;

        int stock = (s.getStk() != null && s.getStk().getQuantity() != null)
                ? s.getStk().getQuantity()
                : 0;

        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(s.getSalesQuantity()));

        return new SalesItemDTO(
                s.getNo(),
                gtin,
                name,
                price,
                s.getSalesQuantity(),
                stock,
                subtotal
        );
    }
}
