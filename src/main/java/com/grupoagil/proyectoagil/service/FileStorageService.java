package com.grupoagil.proyectoagil.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        this.cloudinary = new Cloudinary(config);
    }

    public String storeFile(MultipartFile file) {
        try {
            // Subir a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            // Retornar la URL pública de la imagen
            return (String) uploadResult.get("secure_url");
        } catch (IOException ex) {
            throw new RuntimeException("Error al subir la imagen a Cloudinary", ex);
        }
    }

    public void deleteFile(String imageUrl) {
        try {
            // Extraer el "public_id" de la URL para poder borrarla
            // Ejemplo URL: https://res.cloudinary.com/.../imagen.jpg -> public_id: imagen
            String publicId = obtenerPublicIdDesdeUrl(imageUrl);
            if(publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException ex) {
            System.err.println("No se pudo eliminar la imagen antigua de Cloudinary: " + ex.getMessage());
        }
    }
    
    private String obtenerPublicIdDesdeUrl(String imageUrl) {
        // Lógica simple para extraer el ID del nombre del archivo
        try {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            return fileName.substring(0, fileName.lastIndexOf("."));
        } catch (Exception e) {
            return null;
        }
    }
}
