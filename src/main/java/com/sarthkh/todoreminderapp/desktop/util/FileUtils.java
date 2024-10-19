package com.sarthkh.todoreminderapp.desktop.util;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

public class FileUtils {
    public static MultipartFile convertToMultipartFile(File file) {
        return new MultipartFile() {
            @Override
            @NonNull
            public String getName() {
                return file.getName();
            }

            @Override
            @NonNull
            public String getOriginalFilename() {
                return file.getName();
            }

            @Override
            @NonNull
            public String getContentType() {
                try {
                    return Files.probeContentType(file.toPath());
                } catch (IOException e) {
                    return "application/octet-stream";
                }
            }

            @Override
            public boolean isEmpty() {
                return file.length() == 0;
            }

            @Override
            public long getSize() {
                return file.length();
            }

            @Override
            @NonNull
            public byte[] getBytes() throws IOException {
                return Files.readAllBytes(file.toPath());
            }

            @Override
            @NonNull
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(file);
            }

            @Override
            public void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
                Files.copy(file.toPath(), dest.toPath());
            }
        };
    }
}