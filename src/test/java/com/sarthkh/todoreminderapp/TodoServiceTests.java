package com.sarthkh.todoreminderapp;

import com.sarthkh.todoreminderapp.model.Attachment;
import com.sarthkh.todoreminderapp.model.Todo;
import com.sarthkh.todoreminderapp.repository.TodoRepository;
import com.sarthkh.todoreminderapp.service.AttachmentService;
import com.sarthkh.todoreminderapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TodoServiceTests {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTodo_WithoutReminder_SetsDefaultReminder() throws IOException {
        Todo todo = new Todo();
        todo.setTitle("Test todo");
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        Todo result = todoService.createTodo(todo, new ArrayList<>());

        assertNotNull(result.getReminderDateTime());
        assertTrue(result.getReminderDateTime().isAfter(LocalDateTime.now()));
        assertEquals(Todo.Priority.MEDIUM, result.getPriority());
    }

    @Test
    void updateTodo_ExistingTodo_UpdatesFields() throws IOException {
        Long todoId = 1L;
        Todo existingTodo = new Todo();
        existingTodo.setId(todoId);
        existingTodo.setTitle("Old title");

        Todo updatedTodo = new Todo();
        updatedTodo.setTitle("New title");
        updatedTodo.setDescription("New desc");
        updatedTodo.setReminderDateTime(LocalDateTime.now().plusDays(1));
        updatedTodo.setPriority(Todo.Priority.HIGH);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(existingTodo);

        Todo result = todoService.updateTodo(todoId, updatedTodo, new ArrayList<>());

        assertEquals("New title", result.getTitle());
        assertEquals("New desc", result.getDescription());
        assertEquals(updatedTodo.getReminderDateTime(), result.getReminderDateTime());
        assertEquals(Todo.Priority.HIGH, result.getPriority());
    }

    @Test
    void deleteTodo_WithAttachments_DeletesAllAttachments() throws IOException {
        Long todoId = 1L;
        Todo todo = new Todo();
        todo.setId(todoId);

        Attachment attachment1 = new Attachment();
        attachment1.setId(1L);
        Attachment attachment2 = new Attachment();
        attachment2.setId(2L);

        todo.setAttachments(new ArrayList<>());
        todo.getAttachments().add(attachment1);
        todo.getAttachments().add(attachment2);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        todoService.deleteTodo(todoId);

        verify(attachmentService, times(1)).deleteAttachment(attachment1);
        verify(attachmentService, times(1)).deleteAttachment(attachment2);
        verify(todoRepository, times(1)).delete(todo);
    }
}