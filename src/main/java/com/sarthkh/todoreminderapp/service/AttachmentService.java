package com.sarthkh.todoreminderapp.service;

import com.sarthkh.todoreminderapp.model.Attachment;
import com.sarthkh.todoreminderapp.model.Todo;
import com.sarthkh.todoreminderapp.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository, FileStorageService fileStorageService) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorageService = fileStorageService;
    }

    public Attachment saveAttachment(MultipartFile file, Todo todo) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }

        String fileName = fileStorageService.storeFile(file);

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");
        attachment.setFileType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        attachment.setFilePath(fileName);
        attachment.setTodo(todo);

        return attachmentRepository.save(attachment);
    }

    public Attachment getAttachment(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + id));
    }

    public void deleteAttachment(Attachment attachment) throws IOException {
        if (attachment == null) {
            throw new IllegalArgumentException("Attachment must not be null");
        }

        fileStorageService.deleteFile(attachment.getFilePath());
        attachmentRepository.delete(attachment);
    }
}