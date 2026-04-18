package com.shinu.todo_app.service;

import java.util.List;

import com.shinu.todo_app.dto.TodoDTO;

public interface TodoService {

    TodoDTO createTodo(TodoDTO dto);

    List<TodoDTO> getAllTodos();

    TodoDTO getTodoById(Long id);

    TodoDTO updateTodo(Long id, TodoDTO dto);

    void deleteTodo(Long id);

}