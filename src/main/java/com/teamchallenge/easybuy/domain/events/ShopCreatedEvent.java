package com.teamchallenge.easybuy.domain.events;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 🏪 Событие создания магазина
 */
@Getter
@RequiredArgsConstructor
public class ShopCreatedEvent {
    private final Shop shop;
}