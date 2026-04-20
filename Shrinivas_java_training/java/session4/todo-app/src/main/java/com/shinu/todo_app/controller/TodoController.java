package com.shinu.todo_app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
    // service layer object - business logic is written here
    private final TodoService todoService;

    // constructor injection - Spring will automatically inject service object
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // create new todo
    @PostMapping()
    public ResponseEntity<TodoDTO> createTodo(@Valid @RequestBody TodoDTO dto) {
        logger.info("CREATE TODO API called with title: {}", dto.getTitle());
        TodoDTO createdTodo = todoService.createTodo(dto);
        logger.info("TODO created successfully");
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    // get all todos list
    @GetMapping()
    public ResponseEntity<List<TodoDTO>> getAllTodos() {
        logger.info("GET ALL TODOS API called");
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    // get todo by id
    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id) {
        logger.info("GET TODO BY ID API called with id: {}", id);
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    // update todo by id
    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id, @RequestBody TodoDTO dto) {
        logger.info("UPDATE TODO API called for id: {}", id);
        return ResponseEntity.ok(todoService.updateTodo(id, dto));
    }

    // delete todo by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id) {
        logger.info("DELETE TODO API called for id: {}", id);
        todoService.deleteTodo(id);
        logger.info("TODO deleted successfully with id: {}", id);
        return ResponseEntity.ok("Todo deleted successfully");
    }

}