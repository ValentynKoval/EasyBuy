package com.teamchallenge.easybuy.repo.goods;


import com.teamchallenge.easybuy.models.goods.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
