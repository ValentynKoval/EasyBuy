package com.teamchallenge.easybuy.services.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.exceptions.goods.GoodsNotFoundException;
import com.teamchallenge.easybuy.mapper.goods.GoodsMapper;
import com.teamchallenge.easybuy.models.goods.Goods;
import com.teamchallenge.easybuy.models.goods.category.Category;
import com.teamchallenge.easybuy.repo.goods.GoodsRepository;
import com.teamchallenge.easybuy.repo.goods.GoodsSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    private final GoodsRepository goodsRepository;
    private final GoodsMapper goodsMapper;

    @Autowired
    public GoodsService(GoodsRepository goodsRepository, GoodsMapper goodsMapper) {
        this.goodsRepository = goodsRepository;
        this.goodsMapper = goodsMapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goods", key = "'all'")
    public List<GoodsDTO> getAllGoods() {
        return goodsRepository.findAll().stream()
                .map(goodsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goods", key = "#id")
    public GoodsDTO getGoodsById(UUID id) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException(id));
        return goodsMapper.toDto(goods);
    }

    @Transactional
    @CacheEvict(value = "goods", allEntries = true)
    public GoodsDTO createGoods(GoodsDTO goodsDTO) {
        if (goodsRepository.existsByArt(goodsDTO.getArt())) {
            throw new IllegalArgumentException("Goods with art " + goodsDTO.getArt() + " already exists");
        }
        Goods goods = goodsMapper.toEntity(goodsDTO);
        return goodsMapper.toDto(goodsRepository.save(goods));
    }

    @Transactional
    @CacheEvict(value = "goods", allEntries = true)
    public GoodsDTO updateGoods(UUID id, GoodsDTO goodsDTO) {
        Goods existingGoods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException(id));
        if (!existingGoods.getArt().equals(goodsDTO.getArt()) && goodsRepository.existsByArt(goodsDTO.getArt())) {
            throw new IllegalArgumentException("Goods with art " + goodsDTO.getArt() + " already exists");
        }
        Goods updatedGoods = goodsMapper.toEntity(goodsDTO);
        updatedGoods.setId(id); // Preserve ID
        return goodsMapper.toDto(goodsRepository.save(updatedGoods));
    }

    @Transactional
    @CacheEvict(value = "goods", allEntries = true)
    public void deleteGoods(UUID id) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException(id));
        goodsRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goodsSearch", key = "{#id, #art, #name, #price, #stock, #reviewsCount, #shopId, #category?.id, #goodsStatus, #discountStatus, #rating}")
    public List<GoodsDTO> searchGoods(UUID id, String art, String name, BigDecimal price, Integer stock,
                                      Integer reviewsCount, UUID shopId, Category category,
                                      Goods.GoodsStatus goodsStatus, Goods.DiscountStatus discountStatus, Integer rating) {
        Specification<Goods> spec = Specification.where(GoodsSpecifications.hasId(id))
                .and(GoodsSpecifications.hasArt(art))
                .and(GoodsSpecifications.hasName(name))
                .and(GoodsSpecifications.hasPrice(price))
                .and(GoodsSpecifications.hasStock(stock))
                .and(GoodsSpecifications.hasReviewsCount(reviewsCount))
                .and(GoodsSpecifications.hasShopId(shopId))
                .and(GoodsSpecifications.hasCategory(category))
                .and(GoodsSpecifications.hasGoodsStatus(goodsStatus))
                .and(GoodsSpecifications.hasDiscountStatus(discountStatus))
                .and(GoodsSpecifications.hasRating(rating));
        return goodsRepository.findAll(spec).stream()
                .map(goodsMapper::toDto)
                .collect(Collectors.toList());
    }
}