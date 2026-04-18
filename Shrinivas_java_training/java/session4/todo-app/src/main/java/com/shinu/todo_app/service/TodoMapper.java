package com.shinu.todo_app.service;

import com.shinu.todo_app.dto.TodoDTO;
import com.shinu.todo_app.entity.Todo;

public class TodoMapper {

    public static TodoDTO mapToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setStatus(todo.getStatus());
        return dto;
    }

    public static Todo mapToEntity(TodoDTO dto) {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setStatus(dto.getStatus());
        return todo;
    }

}
