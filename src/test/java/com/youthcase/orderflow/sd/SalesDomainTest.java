package com.youthcase.orderflow.sd;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SalesDomainTest {

//    @Test
//    @DisplayName("SalesHeader - SalesItem 매핑 동작 확인")
//    void salesHeaderItemMapping() {
//        // given
//        SalesHeader header = SalesHeader.builder()
//                .orderId(100L)
//                .totalAmount(0)
//                .salesStatus(SalesStatus.COMPLETED)
//                .build();
//
//        SalesItem item1 = SalesItem.builder()
//                .productName("콜라")
//                .quantity(2)
//                .sdPrice(1500)
//                .salesHeader(header)
//                .build();
//
//        SalesItem item2 = SalesItem.builder()
//                .productName("사이다")
//                .quantity(1)
//                .sdPrice(1200)
//                .salesHeader(header)
//                .build();
//
//        header.setItems(List.of(item1, item2));
//
//        // when
//        int total = header.getItems().stream()
//                .mapToInt(i -> i.getSdPrice() * i.getQuantity())
//                .sum();
//        header.setTotalAmount(total);
//
//        // then
//        assertThat(header.getItems()).hasSize(2);
//        assertThat(header.getTotalAmount()).isEqualTo(4200); // 1500*2 + 1200
//    }
}