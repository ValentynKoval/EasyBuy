package com.teamchallenge.easybuy.services.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsImageDTO;
import com.teamchallenge.easybuy.exceptions.goods.GoodsImageException;
import com.teamchallenge.easybuy.mapper.goods.GoodsImageMapper;
import com.teamchallenge.easybuy.models.goods.GoodsImage;
import com.teamchallenge.easybuy.repo.goods.GoodsImageRepository;
import com.teamchallenge.easybuy.repo.goods.GoodsImageSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GoodsImageService {

    private final GoodsImageRepository goodsImageRepository;
    private final GoodsImageMapper goodsImageMapper;

    @Autowired
    public GoodsImageService(GoodsImageRepository goodsImageRepository, GoodsImageMapper goodsImageMapper) {
        this.goodsImageRepository = goodsImageRepository;
        this.goodsImageMapper = goodsImageMapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goodsImages", key = "'all'")
    public List<GoodsImageDTO> getAllImages() {
        return goodsImageRepository.findAll().stream()
                .map(goodsImageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goodsImages", key = "#id")
    public GoodsImageDTO getImageById(UUID id) {
        GoodsImage image = goodsImageRepository.findById(id)
                .orElseThrow(() -> new GoodsImageException("Image with ID " + id + " not found"));
        return goodsImageMapper.toDto(image);
    }

    @Transactional
    @CacheEvict(value = "goodsImages", allEntries = true)
    public GoodsImageDTO createImage(GoodsImageDTO dto) {
        GoodsImage image = goodsImageMapper.toEntity(dto);
        return goodsImageMapper.toDto(goodsImageRepository.save(image));
    }

    @Transactional
    @CacheEvict(value = "goodsImages", allEntries = true)
    public GoodsImageDTO updateImage(UUID id, GoodsImageDTO dto) {
        GoodsImage existingImage = goodsImageRepository.findById(id)
                .orElseThrow(() -> new GoodsImageException("Image with ID " + id + " not found"));
        GoodsImage updatedImage = goodsImageMapper.toEntity(dto);
        updatedImage.setId(id);
        return goodsImageMapper.toDto(goodsImageRepository.save(updatedImage));
    }

    @Transactional
    @CacheEvict(value = "goodsImages", allEntries = true)
    public void deleteImage(UUID id) {
        GoodsImage image = goodsImageRepository.findById(id)
                .orElseThrow(() -> new GoodsImageException("Image with ID " + id + " not found"));
        goodsImageRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goodsImages", key = "#goodsId")
    public List<GoodsImageDTO> searchImages(UUID goodsId) {
        Specification<GoodsImage> spec = Specification.where(GoodsImageSpecifications.hasGoodsId(goodsId));
        return goodsImageRepository.findAll(spec).stream()
                .map(goodsImageMapper::toDto)
                .collect(Collectors.toList());
    }
}