package com.sarthkh.todoreminderapp.service;

import com.sarthkh.todoreminderapp.model.Attachment;
import com.sarthkh.todoreminderapp.model.Todo;
import com.sarthkh.todoreminderapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;
    private final AttachmentService attachmentService;

    @Autowired
    public TodoService(TodoRepository todoRepository, AttachmentService attachmentService) {
        this.todoRepository = todoRepository;
        this.attachmentService = attachmentService;
    }

    public Todo createTodo(Todo todo, List<MultipartFile> files) throws IOException {
        if (todo.getReminderDateTime() == null) {
//            set reminder to next day morning if not provided
            todo.setReminderDateTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0));
        }

        Todo savedTodo = todoRepository.save(todo);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Attachment attachment = attachmentService.saveAttachment(file, savedTodo);
                savedTodo.addAttachment(attachment);
            }
        }

        return todoRepository.save(savedTodo);
    }

    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo updateTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id) throws IOException {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        for (Attachment attachment : todo.getAttachments()) {
            attachmentService.deleteAttachment(attachment);
        }

        todoRepository.delete(todo);
    }

    public List<Todo> getTodosWithUpcomingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourFromNow = now.plusHours(1);

        return todoRepository.findByReminderDateTimeBetween(now, oneHourFromNow);
    }
}