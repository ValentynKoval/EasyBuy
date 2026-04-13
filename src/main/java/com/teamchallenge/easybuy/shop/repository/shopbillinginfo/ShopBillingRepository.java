package com.teamchallenge.easybuy.shop.repository.shopbillinginfo;

import com.teamchallenge.easybuy.shop.entity.ShopBillingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ShopBillingRepository extends JpaRepository<ShopBillingInfo, UUID> {

}