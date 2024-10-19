package com.sarthkh.todoreminderapp.service;

import com.sarthkh.todoreminderapp.model.Todo;
import com.sarthkh.todoreminderapp.model.Attachment;
import com.sarthkh.todoreminderapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;
    private final AttachmentService attachmentService;

    private static final Duration MAX_SNOOZE = Duration.ofHours(24);

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

        if (todo.getPriority() == null) {
            todo.setPriority(Todo.Priority.MEDIUM);
        }

        Todo savedTodo = todoRepository.save(todo);
        return addAttachmentsToTodo(savedTodo, files);
    }

    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public List<Todo> getTodosByPriority(Todo.Priority priority) {
        return todoRepository.findByPriority(priority);
    }

    public Todo updateTodo(Long id, Todo updatedTodo, List<MultipartFile> files) throws IOException {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        existingTodo.setTitle(updatedTodo.getTitle());
        existingTodo.setDescription(updatedTodo.getDescription());
        existingTodo.setReminderDateTime(updatedTodo.getReminderDateTime());
        existingTodo.setPriority(updatedTodo.getPriority());

        return addAttachmentsToTodo(existingTodo, files);
    }

    private Todo addAttachmentsToTodo(Todo todo, List<MultipartFile> files) throws IOException {
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    Attachment attachment = attachmentService.saveAttachment(file, todo);
                    todo.addAttachment(attachment);
                }
            }
        }
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id) throws IOException {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        for (Attachment attachment : todo.getAttachments()) {
            attachmentService.deleteAttachment(attachment);
        }

        todoRepository.delete(todo);
    }

    public List<Todo> getUpcomingTodos(LocalDateTime start, LocalDateTime end, Todo.Priority priority) {
        return todoRepository.findActiveTodos(start, end, LocalDateTime.now(), priority);
    }

    public Todo snoozeTodo(Long id, Duration duration) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = todo.getReminderDateTime();

        if (duration.compareTo(MAX_SNOOZE) > 0) {
            duration = MAX_SNOOZE;
        }

//        reschedule if past reminder time
        if (now.plus(duration).isAfter(reminderTime)) {
            todo.setReminderDateTime(now.plus(duration));
            todo.setSnoozedUntil(null);  // clear snooze
        } else {
            todo.setSnoozedUntil(now.plus(duration));
        }

        return todoRepository.save(todo);
    }

    public Todo createTestReminder() {
        Todo testTodo = new Todo();
        testTodo.setTitle("reminder test");
        testTodo.setDescription("todo for testing email reminders");
        testTodo.setUserEmail("sarthkh@yahoo.com");
        testTodo.setPriority(Todo.Priority.HIGH);

//        set reminder to 1min from now
        LocalDateTime reminderTime = LocalDateTime.now().plusMinutes(1);
        testTodo.setReminderDateTime(reminderTime);

        return todoRepository.save(testTodo);
    }
}