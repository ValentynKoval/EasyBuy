package com.teamchallenge.easybuy.services.goods.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service for handling Cloudinary image operations.
 */
@Service
public class CloudinaryImageService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryImageService.class);

    private final Cloudinary cloudinary;

    public CloudinaryImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads an image to Cloudinary.
     *
     * @param file Multipart file to be uploaded.
     * @return Public URL of the uploaded image.
     * @throws IOException if upload fails.
     */

    public String uploadImage(MultipartFile file) throws IOException {
        try {
            logger.info("Uploading image to Cloudinary: original filename = {}", file.getOriginalFilename());
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = uploadResult.get("url").toString();
            logger.info("Image uploaded successfully. URL: {}", url);
            return url;
        } catch (IOException e) {
            logger.error("Error uploading image to Cloudinary", e);
            throw new IOException("Failed to upload image to Cloudinary", e);
        }
    }

    /**
     * Deletes an image from Cloudinary by its public ID.
     *
     * @param publicId Public ID of the image.
     * @throws IOException if deletion fails.
     */

    public void deleteImage(String publicId) throws IOException {
        try {
            logger.info("Deleting image from Cloudinary with public ID: {}", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Image deleted successfully.");
        } catch (IOException e) {
            logger.error("Error deleting image from Cloudinary", e);
            throw new IOException("Failed to delete image from Cloudinary", e);
        }
    }

    /**
     * Extracts the public ID from a Cloudinary image URL.
     *
     * @param imageUrl Full Cloudinary image URL.
     * @return Public ID or null if URL is invalid.
     */
    public String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        int lastSlash = imageUrl.lastIndexOf('/');
        if (lastSlash == -1) {
            return null;
        }
        String fileNameWithExtension = imageUrl.substring(lastSlash + 1);
        int dot = fileNameWithExtension.lastIndexOf('.');
        if (dot == -1) {
            return fileNameWithExtension;
        }
        return fileNameWithExtension.substring(0, dot);
    }
}
