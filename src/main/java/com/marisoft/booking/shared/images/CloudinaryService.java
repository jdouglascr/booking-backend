package com.marisoft.booking.shared.images;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.marisoft.booking.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) {
        validateFile(file);

        try {
            String publicId = folder + "/" + UUID.randomUUID();

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", publicId,
                    "resource_type", "image",
                    "overwrite", false,
                    "quality", "auto"
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String secureUrl = (String) uploadResult.get("secure_url");

            log.info("Imagen subida exitosamente a Cloudinary: {}", secureUrl);
            return secureUrl;

        } catch (IOException e) {
            log.error("Error al subir imagen a Cloudinary: {}", e.getMessage(), e);
            throw new BadRequestException("Error al subir la imagen: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Imagen eliminada de Cloudinary: {} - Result: {}", publicId, result.get("result"));
            }
        } catch (Exception e) {
            log.error("Error al eliminar imagen de Cloudinary: {}", e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo no puede estar vacío");
        }

        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new BadRequestException("El archivo excede el tamaño máximo permitido de 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("El archivo debe ser una imagen");
        }

        String filename = file.getOriginalFilename();
        if (filename != null) {
            String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            if (!extension.matches("jpg|jpeg|png|webp|svg")) {
                throw new BadRequestException("Formato de imagen no permitido. Use: jpg, png, webp o svg");
            }
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                return null;
            }

            String afterUpload = parts[1];
            if (afterUpload.matches("v\\d+/.*")) {
                afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
            }

            int lastDotIndex = afterUpload.lastIndexOf(".");
            if (lastDotIndex > 0) {
                afterUpload = afterUpload.substring(0, lastDotIndex);
            }

            return afterUpload;
        } catch (Exception e) {
            log.error("Error al extraer public_id de URL: {}", imageUrl, e);
            return null;
        }
    }
}