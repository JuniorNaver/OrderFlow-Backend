//package com.youthcase.orderflow.po;
//
//import com.youthcase.orderflow.master.price.domain.Price;
//import com.youthcase.orderflow.master.price.repository.PriceRepository;
//import com.youthcase.orderflow.master.product.domain.Product;
//import com.youthcase.orderflow.master.product.repository.ProductRepository;
//import com.youthcase.orderflow.po.domain.POHeader;
//import com.youthcase.orderflow.po.domain.POItem;
//import com.youthcase.orderflow.po.domain.POStatus;
//import com.youthcase.orderflow.po.dto.POItemRequestDTO;
//import com.youthcase.orderflow.po.repository.POHeaderRepository;
//import com.youthcase.orderflow.po.repository.POItemRepository;
//import com.youthcase.orderflow.po.service.POItemService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//class POItemServiceImplTest {
//
//    @Autowired
//    private POItemService poItemService;
//
//    @Autowired
//    private POHeaderRepository poHeaderRepository;
//
//    @Autowired
//    private POItemRepository poItemRepository;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private PriceRepository priceRepository;
//
//    @Test
//    void addPOItemTest() {
//        // given
//        Long poId = 1L;
//        POItemRequestDTO dto = new POItemRequestDTO();
//        String gtin = "";
//
//        dto.setGtin("8809456642756");
//        dto.setOrderQty(5L);
//        dto.setUnitPrice(1L);
//
//        // when
//        var result = poItemService.addPOItem(poId, dto, gtin);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getOrderQty()).isEqualTo(5L);
//        assertThat(result.getTotal()).isGreaterThan(0L);
//    }
//}
