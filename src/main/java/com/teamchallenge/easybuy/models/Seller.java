package com.teamchallenge.easybuy.models;

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
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "store_name")
    private String storeName;
}
