package com.shinu.todo_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shinu.todo_app.dto.TodoDTO;
import com.shinu.todo_app.service.TodoService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping()
    public ResponseEntity<TodoDTO> createTodo(@Valid @RequestBody TodoDTO dto) {
        TodoDTO createdTodo = todoService.createTodo(dto);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<TodoDTO>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id, @Valid @RequestBody TodoDTO dto) {
        return ResponseEntity.ok(todoService.updateTodo(id, dto));
    }

}
