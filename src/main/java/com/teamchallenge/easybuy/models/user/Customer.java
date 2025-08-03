package com.teamchallenge.easybuy.models.user;

import com.teamchallenge.easybuy.models.Address;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
public class Customer extends User{
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "birthday")
    private LocalDate birthday;
}
