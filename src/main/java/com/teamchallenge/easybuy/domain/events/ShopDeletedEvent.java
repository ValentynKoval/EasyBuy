package com.teamchallenge.easybuy.domain.events;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 🗑️ Событие удаления магазина
 */
@Getter
@RequiredArgsConstructor
public class ShopDeletedEvent {
    private final Shop shop;
}