package com.sarthkh.todoreminderapp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    private String description;

    @Email(message = "Invalid email format")
    @NotBlank(message = "User email is required")
    @Column(nullable = false)
    private String userEmail;

    @NotNull(message = "Reminder date time is required")
    @Column(nullable = false)
    private LocalDateTime reminderDateTime;

    @JsonManagedReference
    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    private LocalDateTime snoozedUntil;

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
        attachment.setTodo(this);
    }

    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
        attachment.setTodo(null);
    }

    public void snooze(Duration duration) {
        this.snoozedUntil = LocalDateTime.now().plus(duration);
    }

    public boolean isSnoozeActive() {
        return snoozedUntil != null && snoozedUntil.isAfter(LocalDateTime.now());
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}