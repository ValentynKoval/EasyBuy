package com.teamchallenge.easybuy.repository.shop.shopcontact;

import com.teamchallenge.easybuy.domain.model.enums.ContactMethod;

public interface ContactStat {

    Long getCount();

    ContactMethod getMethod();
}