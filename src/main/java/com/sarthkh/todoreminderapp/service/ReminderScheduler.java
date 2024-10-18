package com.sarthkh.todoreminderapp.service;

import com.sarthkh.todoreminderapp.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    private final TodoService todoService;
    private final EmailService emailService;

    @Autowired
    public ReminderScheduler(TodoService todoService, EmailService emailService) {
        this.todoService = todoService;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 60000) // run every minute
    public void checkAndSendReminders() {
        logger.info("Reminder check at {}", LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourFromNow = now.plusHours(1);

        List<Todo> upcomingTodos = todoService.getUpcomingTodos(now, oneHourFromNow, null);
        logger.info("{} todos with upcoming reminders", upcomingTodos.size());

        for (Todo todo : upcomingTodos) {
            sendReminderEmail(todo);
        }

        logger.info("reminders sent");
    }

    private void sendReminderEmail(Todo todo) {
        if (todo.isSnoozeActive()) {
            logger.info("Todo {} snoozed until {} skipping.", todo.getId(), todo.getSnoozedUntil());
            return;
        }

        String to = todo.getUserEmail();
        String subject = "Reminder: " + todo.getTitle();
        String body = "Don't forget about your task: " + todo.getTitle() + "\n" +
                "Description: " + todo.getDescription() + "\n" +
                "Due at: " + todo.getReminderDateTime() + "\n" +
                "Priority: " + todo.getPriority();

        logger.info("sending reminder for todo: {} to {}", todo.getId(), to);
        emailService.sendEmail(to, subject, body);
        logger.info("reminder sent for todo: {}", todo.getId());
    }
}