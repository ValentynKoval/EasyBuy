package com.teamchallenge.easybuy.domain.model.user;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"shop", "seller"})
@ToString(exclude = {"shop", "seller"})
@Entity
public class Manager extends User {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id")
    private Seller seller;
}