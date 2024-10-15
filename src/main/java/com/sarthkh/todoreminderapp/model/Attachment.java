package com.sarthkh.todoreminderapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Lob
    @Column(nullable = false)
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;
}