package com.shinu.todo_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shinu.todo_app.entity.Todo;
import com.shinu.todo_app.repository.TodoRepository;

@ExtendWith(MockitoExtension.class) // enables Mockito annotations
public class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository; // mocking repository no real DB used

    @InjectMocks
    private TodoServiceImpl todoService; // injecting mock repo into service

    @Test
    void testCreateTodo() {

        Todo todo = new Todo(); // creating dummy todo object
        todo.setTitle("Learn Spring Boot"); // setting sample title

        when(todoRepository.save(any(Todo.class))).thenReturn(todo); // mocking save method

        var result = todoService.createTodo(
                new com.shinu.todo_app.dto.TodoDTO()); // calling service method

        assertNotNull(result); // checking result is not null

        verify(todoRepository, times(1)).save(any(Todo.class)); // verifying save called once
    }

    @Test
    void testGetTodoById() {

        Todo todo = new Todo(); // creating sample todo
        todo.setTitle("Test Todo"); // setting title

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo)); // mocking findById

        var result = todoService.getTodoById(1L); // calling service method

        assertNotNull(result); // checking result is not null

        verify(todoRepository).findById(1L); // verifying repository call
    }

    @Test
    void testGetAllTodos() {

        Todo todo = new Todo(); // creating sample todo
        todo.setTitle("Task 1"); // setting title

        when(todoRepository.findAll())
                .thenReturn(List.of(todo)); // mocking list return

        var result = todoService.getAllTodos(); // calling service method

        assertEquals(1, result.size()); // checking list size

        verify(todoRepository).findAll(); // verifying method call
    }

    @Test
    void testDeleteTodo() {

        Todo todo = new Todo(); // creating sample todo
        todo.setId(1L); // setting id

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo)); // mocking findById

        doNothing().when(todoRepository).delete(todo); // mocking delete operation

        todoService.deleteTodo(1L); // calling service method

        verify(todoRepository).delete(todo); // verifying delete called
    }

}