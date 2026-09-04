package com.sistemapdv.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
public class CloudinaryService {

    private static final long MAX_SIZE = 2 * 1024 * 1024;

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "La imagen del producto es obligatoria"
            );
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "La imagen no puede superar los 2 MB"
            );
        }

        String fileName = file.getOriginalFilename();

        if(fileName == null || !tieneExtensionValida(fileName)){
            throw new IllegalArgumentException("La imagen debe ser JPG, JPEG o PNG");
        }

        try{
            BufferedImage image = ImageIO.read(file.getInputStream());

            if(image == null){
                throw new IllegalArgumentException("El archivo no es una imagen válida");
            }
        }catch (IOException e){

            throw new RuntimeException("No se pudo validar la imagen");
        }
    }

    private boolean tieneExtensionValida(String fileName){

        String name = fileName.toLowerCase();

        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }

    public String uploadImage(MultipartFile file){

        validateImage(file);

        try{
            Map result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "Romi-Insumos/productos",
                            "resource_type", "image"
                    )
            );

            return result.get("secure_url").toString();

        }catch(IOException e){
            throw new RuntimeException("No se pudo subir la imagen a Cloudinary", e);
        }
    }
}
