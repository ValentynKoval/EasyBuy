package com.teamchallenge.easybuy.services;

import com.teamchallenge.easybuy.models.Shop;
import com.teamchallenge.easybuy.models.User;
import com.teamchallenge.easybuy.repo.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    public Shop createShop(User user, String shopName) {
        Shop shop = Shop.builder()
                .user(user)
                .shop_name(shopName)
                .build();
        return shopRepository.save(shop);
    }
}
