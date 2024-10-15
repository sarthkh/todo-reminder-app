package com.sarthkh.todoreminderapp.service;

import com.sarthkh.todoreminderapp.model.Attachment;
import com.sarthkh.todoreminderapp.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public Attachment saveAttachment(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
//        create unique filename if missing
        if (fileName == null || fileName.isEmpty()) {
            fileName = "attachment_" + UUID.randomUUID();
        } else {
            fileName = StringUtils.cleanPath(fileName);
        }

        Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileType(file.getContentType());
        attachment.setData(file.getBytes());

        return attachmentRepository.save(attachment);
    }

    public Attachment getAttachment(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
    }
}