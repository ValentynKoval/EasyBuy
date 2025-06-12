package com.teamchallenge.easybuy.services.goods.image;

import com.teamchallenge.easybuy.dto.goods.GoodsImageDTO;
import com.teamchallenge.easybuy.exceptions.goods.GoodsImageException;
import com.teamchallenge.easybuy.mapper.goods.GoodsImageMapper;
import com.teamchallenge.easybuy.models.goods.Goods;
import com.teamchallenge.easybuy.models.goods.GoodsImage;
import com.teamchallenge.easybuy.repo.goods.GoodsImageRepository;
import com.teamchallenge.easybuy.repo.goods.GoodsImageSpecifications;
import com.teamchallenge.easybuy.repo.goods.GoodsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for handling image operations related to goods.
 * Includes Cloudinary integration, persistence, and caching.
 */
@Service
@Tag(name = "Goods Image Service", description = "Business logic for uploading, updating, retrieving and deleting goods images")
public class GoodsImageService {

    private final GoodsImageRepository goodsImageRepository;
    private final GoodsImageMapper goodsImageMapper;
    private final CloudinaryImageService cloudinaryImageService;
    private final GoodsRepository goodsRepository;

    @Autowired
    public GoodsImageService(GoodsImageRepository goodsImageRepository,
                             GoodsImageMapper goodsImageMapper,
                             CloudinaryImageService cloudinaryImageService,
                             GoodsRepository goodsRepository) {
        this.goodsImageRepository = goodsImageRepository;
        this.goodsImageMapper = goodsImageMapper;
        this.cloudinaryImageService = cloudinaryImageService;
        this.goodsRepository = goodsRepository;
    }

    /**
     * Retrieves all goods images from the database.
     *
     * @return list of GoodsImageDTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "goodsImages", key = "'all'")
    @Operation(summary = "Get all goods images", description = "Returns all images stored for all goods.")
    public List<GoodsImageDTO> getAllImages() {
        return goodsImageRepository.findAll().stream()
                .map(goodsImageMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a goods image by its unique identifier.
     *
     * @param id image UUID
     * @return GoodsImageDTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "goodsImages", key = "#id")
    @Operation(summary = "Get image by ID", description = "Returns a single image by its unique ID.")
    public GoodsImageDTO getImageById(UUID id) {
        GoodsImage image = goodsImageRepository.findById(id)
                .orElseThrow(() -> new GoodsImageException("Image with ID " + id + " not found"));
        return goodsImageMapper.toDto(image);
    }

    /**
     * Uploads a new image to Cloudinary and stores reference in DB.
     *
     * @param goodsId ID of the related goods entity
     * @param file    uploaded image
     * @return created GoodsImageDTO
     * @throws IOException on upload failure
     */
    @Transactional
    @CacheEvict(value = "goodsImages", allEntries = true)
    @Operation(summary = "Create image", description = "Uploads image to Cloudinary and saves reference in DB.")
    public GoodsImageDTO createImage(UUID goodsId, MultipartFile file) throws IOException {
        // Step 1: Get the Goods object to get the shopId and art
        Goods goods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new GoodsImageException("Goods with ID " + goodsId + " not found for image creation"));

        // Step 2: Upload image to Cloudinary from shopId and art
        String imageUrl = cloudinaryImageService.uploadImage(file, goods.getShopId(), goods.getArt());

        // Step 3: Create entity
        GoodsImage newImage = new GoodsImage();
        newImage.setImageUrl(imageUrl);
        newImage.setGoods(goods);

        // Step 4: Save in DB
        return goodsImageMapper.toDto(goodsImageRepository.save(newImage));
    }

    /**
     * Updates an existing image by replacing it in Cloudinary and updating the DB.
     *
     * @param id      image ID to update
     * @param goodsId related goods ID (currently unused but reserved, but will be used to get shopId and art)
     * @param file    new image file
     * @return updated GoodsImageDTO
     * @throws IOException on upload/delete failure
     */
    @Transactional
    @CacheEvict(value = "goodsImages", allEntries = true)
    @Operation(summary = "Update image", description = "Replaces existing image in Cloudinary and updates DB reference.")
    public GoodsImageDTO updateImage(UUID id, UUID goodsId, MultipartFile file) throws IOException {
        GoodsImage existingImage = goodsImageRepository.findById(id)
                .orElseThrow(() -> new GoodsImageException("Image with ID " + id + " not found"));

        // We get the Goods object to get the shopId and art for the new upload
        Goods goods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new GoodsImageException("Goods with ID " + goodsId + " not found for image update"));

        // Step 1: Delete old image from Cloudinary
        String oldPublicId = cloudinaryImageService.extractPublicIdFromUrl(existingImage.getImageUrl());
        if (oldPublicId != null) {
            cloudinaryImageService.deleteImage(oldPublicId);
        }

        // Step 2: Upload new image з shopId та art
        String newImageUrl = cloudinaryImageService.uploadImage(file, goods.getShopId(), goods.getArt());

        // Step 3: Update and persist entity
        existingImage.setImageUrl(newImageUrl);

        return goodsImageMapper.toDto(goodsImageRepository.save(existingImage));
    }

    /**
     * Deletes an image from both Cloudinary and the database.
     *
     * @param id image ID to delete
     * @throws IOException on deletion error
     */
    @Transactional
    @CacheEvict(value = "goodsImages", allEntries = true)
    @Operation(summary = "Delete image", description = "Deletes image from Cloudinary and removes reference from DB.")
    public void deleteImage(UUID id) throws IOException {
        GoodsImage image = goodsImageRepository.findById(id)
                .orElseThrow(() -> new GoodsImageException("Image with ID " + id + " not found"));

        // Step 1: Delete from Cloudinary
        String publicId = cloudinaryImageService.extractPublicIdFromUrl(image.getImageUrl());
        if (publicId != null) {
            cloudinaryImageService.deleteImage(publicId);
        }

        // Step 2: Delete from DB
        goodsImageRepository.deleteById(id);
    }

    /**
     * Searches all images attached to a specific goods ID.
     *
     * @param goodsId ID of the goods
     * @return list of GoodsImageDTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "goodsImages", key = "#goodsId")
    @Operation(summary = "Search images by goods ID", description = "Finds all images attached to a specific goods item.")
    public List<GoodsImageDTO> searchImages(UUID goodsId) {
        Specification<GoodsImage> spec = Specification.where(GoodsImageSpecifications.hasGoodsId(goodsId));
        return goodsImageRepository.findAll(spec).stream()
                .map(goodsImageMapper::toDto)
                .collect(Collectors.toList());
    }
}