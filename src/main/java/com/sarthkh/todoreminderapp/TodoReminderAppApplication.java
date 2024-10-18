package com.sarthkh.todoreminderapp;

import com.sarthkh.todoreminderapp.desktop.TodoDesktopApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TodoReminderAppApplication {

    public static void main(String[] args) {
        Application.launch(TodoDesktopApplication.class, args);
    }

}