package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.domain.ShopList;
import com.youthcase.orderflow.pr.repository.ShopListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopListService {

    private final ShopListRepository shopListRepository;

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
