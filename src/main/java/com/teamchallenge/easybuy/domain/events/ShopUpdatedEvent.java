package com.teamchallenge.easybuy.domain.events;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ✏️ Событие обновления магазина
 */
@Getter
@RequiredArgsConstructor
public class ShopUpdatedEvent {
    private final Shop shop;
}