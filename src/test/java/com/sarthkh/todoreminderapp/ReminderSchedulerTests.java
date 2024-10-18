package com.sarthkh.todoreminderapp;

import com.sarthkh.todoreminderapp.model.Todo;
import com.sarthkh.todoreminderapp.service.EmailService;
import com.sarthkh.todoreminderapp.service.ReminderScheduler;
import com.sarthkh.todoreminderapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReminderSchedulerTests {

    @Mock
    private TodoService todoService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReminderScheduler reminderScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkAndSendReminders_WithUpcomingTodos_SendsEmails() {
        LocalDateTime now = LocalDateTime.now();
        Todo upcomingTodo1 = createTodo(1L, "Upcoming todo 1", "user1@temp.com", now.plusMinutes(30));
        Todo upcomingTodo2 = createTodo(2L, "Upcoming todo 2", "user2@temp.com", now.plusMinutes(45));

        when(todoService.getUpcomingTodos(any(), any(), isNull()))
                .thenReturn(Arrays.asList(upcomingTodo1, upcomingTodo2));

        reminderScheduler.checkAndSendReminders();

        verify(emailService, times(1)).sendEmail(eq("user1@temp.com"),
                eq("Reminder: Upcoming todo 1"), contains("Upcoming todo 1"));
        verify(emailService, times(1)).sendEmail(eq("user2@temp.com"),
                eq("Reminder: Upcoming todo 2"), contains("Upcoming todo 2"));
    }

    @Test
    void checkAndSendReminders_WithSnoozedTodo_DoesNotSendEmail() {
        LocalDateTime now = LocalDateTime.now();
        Todo snoozedTodo = createTodo(1L, "Snoozed todo", "user@temp.com", now.plusMinutes(30));
        snoozedTodo.setSnoozedUntil(now.plusHours(1));

        when(todoService.getUpcomingTodos(any(), any(), isNull()))
                .thenReturn(Collections.singletonList(snoozedTodo));

        reminderScheduler.checkAndSendReminders();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    private Todo createTodo(Long id, String title, String email, LocalDateTime reminderDateTime) {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setUserEmail(email);
        todo.setReminderDateTime(reminderDateTime);
        return todo;
    }
}