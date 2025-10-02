package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.domain.ShopList;
import com.youthcase.orderflow.pr.repository.ShopListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopListService {

    private final ShopListRepository shopListRepository;
    public ShopList createShopListWithDueDate(ShopList shopList) {
        // 1. 발주일 가져오기 (DTO나 엔티티에서 orderDate 필드 필요)
        LocalDate orderDate = shopList.getOrderDate();

        // 2. 상품의 StorageMethod에서 소요일 가져오기
        int leadTime = shopList.getProduct().getStorageMethod().getLeadTimeDays();

        // 3. 예상 도착일 계산
        LocalDate dueDate = orderDate.plusDays(leadTime);

        // 4. 문자열 생성
        String message = dueDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + "에 발주될 예정입니다.";
        shopList.setLeadTimeDays(message);

        // 5. 저장
        return shopListRepository.save(shopList);
    }

    public List<ShopList> getAllShopLists() {
        return shopListRepository.findAll();
    }

    public Optional<ShopList> getShopListById(Long id) {
        return shopListRepository.findById(id);
    }

    public ShopList saveShopList(ShopList shopList) {
        return shopListRepository.save(shopList);
    }

    public void deleteShopList(Long id) {
        shopListRepository.deleteById(id);
    }
}
