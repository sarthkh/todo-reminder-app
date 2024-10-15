package com.sarthkh.todoreminderapp.repository;

import com.sarthkh.todoreminderapp.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}