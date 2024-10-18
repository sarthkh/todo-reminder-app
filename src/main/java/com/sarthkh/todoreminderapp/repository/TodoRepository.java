package com.sarthkh.todoreminderapp.repository;

import com.sarthkh.todoreminderapp.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("SELECT t FROM Todo t WHERE t.reminderDateTime BETWEEN :start AND :end " +
            "AND (t.snoozedUntil IS NULL OR t.snoozedUntil < :now)" +
            "AND (:priority IS NULL OR t.priority = :priority)")
    List<Todo> findActiveTodos(LocalDateTime start, LocalDateTime end, LocalDateTime now, Todo.Priority priority);

    List<Todo> findByPriority(Todo.Priority priority);
}