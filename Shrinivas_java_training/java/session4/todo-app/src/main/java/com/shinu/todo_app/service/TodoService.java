package com.shinu.todo_app.service;

import java.util.List;

import com.shinu.todo_app.dto.TodoDTO;

// This interface defines the business logic methods for Todo application
// It acts as a contract for the service implementation class

public interface TodoService {

    // method to create a new todo
    TodoDTO createTodo(TodoDTO dto);

    // method to get all todos from database
    List<TodoDTO> getAllTodos();

    // method to get a single todo by its id
    TodoDTO getTodoById(Long id);

    // method to update existing todo by id
    TodoDTO updateTodo(Long id, TodoDTO dto);

    // method to delete todo by id
    void deleteTodo(Long id);

}