package com.teamchallenge.easybuy.shop.event;

import com.teamchallenge.easybuy.shop.entity.Shop;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Store deletion event
 */
@Getter
@RequiredArgsConstructor
public class ShopDeletedEvent {
    private final Shop shop;
}