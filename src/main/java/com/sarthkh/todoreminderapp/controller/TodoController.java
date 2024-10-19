package com.sarthkh.todoreminderapp.controller;

import com.sarthkh.todoreminderapp.model.Todo;
import com.sarthkh.todoreminderapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Todo> createTodo(
            @RequestPart("todo") Todo todo,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            Todo createdTodo = todoService.createTodo(todo, files);
            return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id)
                .map(todo -> new ResponseEntity<>(todo, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        List<Todo> todos = todoService.getAllTodos();
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Todo>> getTodosByPriority(@PathVariable Todo.Priority priority) {
        List<Todo> todos = todoService.getTodosByPriority(priority);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Todo> updateTodo(
            @PathVariable Long id,
            @RequestPart("todo") Todo todo,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            Todo updatedTodo = todoService.updateTodo(id, todo, files);
            return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        try {
            todoService.deleteTodo(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Todo>> getUpcomingTodos(
            @RequestParam(required = false) Todo.Priority priority
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(1);
        List<Todo> todos = todoService.getUpcomingTodos(now, end, priority);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @PostMapping("/{id}/snooze")
    public ResponseEntity<Todo> snoozeTodo(@PathVariable Long id, @RequestParam Duration duration) {
        Todo snoozedTodo = todoService.snoozeTodo(id, duration);
        return new ResponseEntity<>(snoozedTodo, HttpStatus.OK);
    }

    @PostMapping("/test-reminder")
    public ResponseEntity<Map<String, Object>> createTestReminder() {
        try {
            Todo testTodo = todoService.createTestReminder();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test reminder created successfully");
            response.put("reminderTime", testTodo.getReminderDateTime());
            response.put("emailTo", testTodo.getUserEmail());
            response.put("todo", testTodo);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}