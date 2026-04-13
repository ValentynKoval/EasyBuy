package com.teamchallenge.easybuy.shop.event;

import com.teamchallenge.easybuy.shop.entity.Shop;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Store creation event
 */
@Getter
@RequiredArgsConstructor
public class ShopCreatedEvent {
    private final Shop shop;
}