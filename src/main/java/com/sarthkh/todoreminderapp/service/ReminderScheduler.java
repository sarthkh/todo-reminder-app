package com.sarthkh.todoreminderapp.service;

import com.sarthkh.todoreminderapp.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {

    private final TodoService todoService;
    private final EmailService emailService;

    @Autowired
    public ReminderScheduler(TodoService todoService, EmailService emailService) {
        this.todoService = todoService;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 60000) // run every minute
    public void checkAndSendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourFromNow = now.plusHours(1);

        List<Todo> upcomingTodos = todoService.getTodosWithUpcomingReminders(now, oneHourFromNow);

        for (Todo todo : upcomingTodos) {
            sendReminderEmail(todo);
        }
    }

    private void sendReminderEmail(Todo todo) {
        String to = todo.getUserEmail();
        String subject = "Reminder: " + todo.getTitle();
        String body = "Don't forget about your task: " + todo.getTitle() + "\n" +
                "Description: " + todo.getDescription() + "\n" +
                "Due at: " + todo.getReminderDateTime();

        emailService.sendEmail(to, subject, body);
    }
}