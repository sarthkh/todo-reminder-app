package com.sarthkh.todoreminderapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    //    store uploaded file and return its unique name
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");
        String fileExtension = StringUtils.getFilenameExtension(fileName);
        fileExtension = fileExtension != null ? fileExtension : "";
        String uniqueFileName = UUID.randomUUID() + "." + fileExtension;

//        set upload path, file location
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path targetLocation = uploadPath.resolve(uniqueFileName);

        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Could not store file " + fileName + " try again.", ex);
        }

        return uniqueFileName;
    }

    public void deleteFile(String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name must not be null or empty");
        }

        Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                throw new IOException("File " + fileName + " not found");
            }
        } catch (IOException ex) {
            throw new IOException("Could not delete file " + fileName + " try again.", ex);
        }
    }
}