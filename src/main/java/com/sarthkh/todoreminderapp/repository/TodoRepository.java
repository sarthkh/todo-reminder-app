package com.sarthkh.todoreminderapp.repository;

import com.sarthkh.todoreminderapp.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByReminderDateTimeBetween(LocalDateTime start, LocalDateTime end);
}