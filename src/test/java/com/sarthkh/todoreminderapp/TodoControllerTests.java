package com.sarthkh.todoreminderapp;

import com.sarthkh.todoreminderapp.controller.TodoController;
import com.sarthkh.todoreminderapp.model.Todo;
import com.sarthkh.todoreminderapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TodoControllerTests {

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTodo_ValidInput_ReturnsCreatedTodo() throws IOException {
        Todo inputTodo = createTodo(null, "New todo", "todo description", "user@temp.com", LocalDateTime.now().plusDays(1));
        Todo createdTodo = createTodo(1L, "New todo", "todo description", "user@temp.com", LocalDateTime.now().plusDays(1));

        List<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes()));

        when(todoService.createTodo(any(Todo.class), anyList())).thenReturn(createdTodo);

        ResponseEntity<Todo> response = todoController.createTodo(inputTodo, files);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdTodo, response.getBody());
        verify(todoService, times(1)).createTodo(any(Todo.class), anyList());
    }

    @Test
    void getTodoById_ExistingId_ReturnsTodo() {
        Long todoId = 1L;
        Todo todo = createTodo(todoId, "Existing todo", "description", "user@temp.com", LocalDateTime.now().plusDays(1));

        when(todoService.getTodoById(todoId)).thenReturn(Optional.of(todo));

        ResponseEntity<Todo> response = todoController.getTodoById(todoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(todo, response.getBody());
    }

    @Test
    void updateTodo_ValidInput_ReturnsUpdatedTodo() throws IOException {
        Long todoId = 1L;
        Todo inputTodo = createTodo(null, "Updated todo", "Updated description", "user@temp.com", LocalDateTime.now().plusDays(2));
        Todo updatedTodo = createTodo(todoId, "Updated todo", "Updated description", "user@temp.com", LocalDateTime.now().plusDays(2));

        List<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("file", "updated.txt", MediaType.TEXT_PLAIN_VALUE, "updated content".getBytes()));

        when(todoService.updateTodo(eq(todoId), any(Todo.class), anyList())).thenReturn(updatedTodo);

        ResponseEntity<Todo> response = todoController.updateTodo(todoId, inputTodo, files);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTodo, response.getBody());
        verify(todoService, times(1)).updateTodo(eq(todoId), any(Todo.class), anyList());
    }

    private Todo createTodo(Long id, String title, String description, String email, LocalDateTime reminderDateTime) {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setUserEmail(email);
        todo.setReminderDateTime(reminderDateTime);
        return todo;
    }
}