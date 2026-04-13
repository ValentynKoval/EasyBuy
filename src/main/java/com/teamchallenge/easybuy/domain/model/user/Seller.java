package com.teamchallenge.easybuy.domain.model.user;

import com.teamchallenge.easybuy.domain.model.Address;
import com.teamchallenge.easybuy.domain.model.shop.Shop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class Seller extends User {
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;
}
