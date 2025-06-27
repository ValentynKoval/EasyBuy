package com.teamchallenge.easybuy.models;

import com.teamchallenge.easybuy.models.goods.Shop;
import com.teamchallenge.easybuy.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class Seller extends User{
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Shop shop;
}
