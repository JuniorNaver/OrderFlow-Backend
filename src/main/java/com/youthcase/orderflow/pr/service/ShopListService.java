package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.DTO.ShopListRequestDto;
import com.youthcase.orderflow.pr.DTO.ShopListResponseDto;
import com.youthcase.orderflow.pr.domain.AvailableStatus;
import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.domain.ShopList;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import com.youthcase.orderflow.pr.repository.ShopListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopListService {

    private final ShopListRepository shopListRepository;
    private final ProductRepository productRepository;

    // Create
    public ShopListResponseDto createShopList(ShopListRequestDto dto) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        int leadTime = product.getStorageMethod().getLeadTimeDays();
        LocalDate dueDate = dto.orderDate().plusDays(leadTime);
        String deliveryMessage = dueDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + "에 발주될 예정입니다.";

        ShopList shopList = new ShopList();
        shopList.setProduct(product);
        shopList.setProductImage(dto.productImage());
        shopList.setProductDescription(dto.productDescription());
        shopList.setOrderDate(dto.orderDate());
        shopList.setDeliveryMessage(deliveryMessage);
        // 재고에 따른 발주 가능 여부 설정
        if (product.getStockQuantity() > 0) {
            shopList.setAvailable(AvailableStatus.AVAILABLE);
        } else {
            shopList.setAvailable(AvailableStatus.UNAVAILABLE);
        }

        ShopList saved = shopListRepository.save(shopList);

        return mapToResponseDto(saved);
    }

    // Read All
    public List<ShopListResponseDto> getAllShopLists() {
        return shopListRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Read One
    public ShopListResponseDto getShopListById(Long id) {
        ShopList shopList = shopListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ShopList를 찾을 수 없습니다."));
        return mapToResponseDto(shopList);
    }

    // Update
    public ShopListResponseDto updateShopList(Long id, ShopListRequestDto dto) {
        ShopList shopList = shopListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ShopList를 찾을 수 없습니다."));

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        int leadTime = product.getStorageMethod().getLeadTimeDays();
        LocalDate dueDate = dto.orderDate().plusDays(leadTime);
        String deliveryMessage = dueDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + "에 발주될 예정입니다.";

        shopList.setProduct(product);
        shopList.setProductImage(dto.productImage());
        shopList.setProductDescription(dto.productDescription());
        shopList.setOrderDate(dto.orderDate());
        shopList.setDeliveryMessage(deliveryMessage);
        // 재고에 따른 발주 가능 여부 설정
        if (product.getStockQuantity() > 0) {
            shopList.setAvailable(AvailableStatus.AVAILABLE);
        } else {
            shopList.setAvailable(AvailableStatus.UNAVAILABLE);
        }

        ShopList updated = shopListRepository.save(shopList);

        return mapToResponseDto(updated);
    }

    // Delete
    public void deleteShopList(Long id) {
        shopListRepository.deleteById(id);
    }

    // DTO 매핑 헬퍼
    private ShopListResponseDto mapToResponseDto(ShopList shopList) {
        return new ShopListResponseDto(
                shopList.getPrItemId(),
                shopList.getProductImage(),
                shopList.getProductDescription(),
                shopList.getOrderDate(),
                shopList.getDeliveryMessage(),
                shopList.getProduct().getUnit(),
                shopList.getAvailable()
        );
    }
}