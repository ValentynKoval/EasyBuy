package com.teamchallenge.easybuy.shop.event;

import com.teamchallenge.easybuy.shop.entity.Shop;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Store Update Event
 */
@Getter
@RequiredArgsConstructor
public class ShopUpdatedEvent {
    private final Shop shop;
}