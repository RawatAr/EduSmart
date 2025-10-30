package com.edusmart.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Service for image processing operations
 */
@Service
@Slf4j
public class ImageProcessingService {

    /**
     * Resize image
     */
    public byte[] resizeImage(MultipartFile file, int targetWidth, int targetHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        
        if (originalImage == null) {
            throw new IOException("Invalid image file");
        }

        // Calculate aspect ratio
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        double aspectRatio = (double) originalWidth / originalHeight;
        
        if (targetWidth == 0) {
            targetWidth = (int) (targetHeight * aspectRatio);
        }
        if (targetHeight == 0) {
            targetHeight = (int) (targetWidth / aspectRatio);
        }

        // Resize image
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        
        return baos.toByteArray();
    }

    /**
     * Create thumbnail
     */
    public byte[] createThumbnail(MultipartFile file) throws IOException {
        return resizeImage(file, 300, 200);
    }

    /**
     * Validate image
     */
    public boolean isValidImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException e) {
            log.error("Error validating image", e);
            return false;
        }
    }
}
