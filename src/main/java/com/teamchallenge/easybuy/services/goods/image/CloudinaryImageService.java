package com.teamchallenge.easybuy.services.goods.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

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
     * Uploads an image to Cloudinary into a specific folder structure.
     * The folder path will be: "easybuy/shops/{shopId}/goods/{goodsArt}"
     *
     * @param file    Multipart file to be uploaded.
//     * @param shopId  The UUID of the shop, used for folder creation.
//     * @param goodsArt The article number of the goods, used for folder creation.
     * @return Public URL of the uploaded image.
     * @throws IOException if upload fails.
     */

    public String uploadImage(MultipartFile file, String folderPath) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty, cannot upload.");
        }
        try {
            logger.info("Uploading image to Cloudinary: original filename = {}, folder = {}", file.getOriginalFilename(), folderPath);

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderPath,
                            "resource_type", "image"
                    ));
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
        if (publicId == null || publicId.isEmpty()) {
            logger.warn("Attempted to delete image with null or empty public ID. Skipping deletion.");
            return;
        }
        try {
            logger.info("Deleting image from Cloudinary with public ID: {}", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Image deleted successfully.");
        } catch (IOException e) {
            logger.error("Error deleting image from Cloudinary with public ID: {}", publicId, e);
            throw new IOException("Failed to delete image from Cloudinary", e);
        }
    }

    /**
     * Extracts the public ID from a Cloudinary image URL.
     * This method is updated to correctly parse public IDs that include folder paths.
     *
     * @param imageUrl Full Cloudinary image URL.
     * @return Public ID or null if URL is invalid.
     */

    public String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        try {
            // Cloudinary URL format:
            // https://res.cloudinary.com/<cloud_name>/image/upload/[v<version_number>]/<public_id_with_folders>.<extension>
            // For example: https://res.cloudinary.com/easybuymarketplace/image/upload/v123456789/easybuy/shops/shop_id/goods/art_id/image_name.jpg
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }
            String afterUpload = imageUrl.substring(uploadIndex + "/upload/".length());

            // Remove the version part (v<numbers>/), if it exists
            int versionEndIndex = afterUpload.indexOf("/");
            if (versionEndIndex != -1 && afterUpload.substring(0, versionEndIndex).matches("v\\d+")) {
                afterUpload = afterUpload.substring(versionEndIndex + 1);
            }

            // Find the last dot for the file extension
            int dotIndex = afterUpload.lastIndexOf(".");
            if (dotIndex != -1) {
                return afterUpload.substring(0, dotIndex);
            }
            return afterUpload;
        } catch (Exception e) {
            logger.error("Error extracting public ID from URL: {}", imageUrl, e);
            return null;
        }
    }

    private String getInitials(String name) {
        String[] words = name.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].substring(0, Math.min(2, words[0].length())).toUpperCase();
        } else {
            return (words[0].substring(0,1) + words[1].substring(0,1)).toUpperCase();
        }
    }

    private String randomColorHex() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("%02X%02X%02X", r, g, b);
    }

    private String pickTextColor(String bgHex) {
        int r = Integer.parseInt(bgHex.substring(0,2), 16);
        int g = Integer.parseInt(bgHex.substring(2,4), 16);
        int b = Integer.parseInt(bgHex.substring(4,6), 16);
        double luminance = (0.299*r + 0.587*g + 0.114*b) / 255;
        return (luminance > 0.5) ? "000000" : "FFFFFF";
    }

    public String generateAvatarUrl(String name) {
        String initials = getInitials(name);
        String bgColor = randomColorHex();
        String textColor = pickTextColor(bgColor);

        String cloudName = cloudinary.config.cloudName;

        return String.format("https://res.cloudinary.com/%s/image/upload/"+
                        "w_400,h_400,c_fill,b_rgb:%s,co_rgb:%s," +
                        "l_text:Roboto_400_bold:%s/empty",
                cloudName, bgColor, textColor, initials);
    }
}