package com.bookstory.store.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DefaultImageService implements ImageService {

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    public String saveImage(final String fileName, final byte[] data) throws IOException {
        try {
            Path imagePath = Paths.get(uploadDir, fileName);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, data);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
        return "/uploads/images/" + fileName;
    }
}

