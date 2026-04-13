package com.teamchallenge.easybuy.repository.shop.shopbillinginfo;

import com.teamchallenge.easybuy.domain.model.shop.ShopBillingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ShopBillingRepository extends JpaRepository<ShopBillingInfo, UUID> {

}