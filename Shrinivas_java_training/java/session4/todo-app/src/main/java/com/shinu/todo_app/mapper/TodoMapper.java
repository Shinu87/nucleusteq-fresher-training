package com.shinu.todo_app.mapper;

import com.shinu.todo_app.dto.TodoDTO;
import com.shinu.todo_app.entity.Todo;

// This class is used to convert Entity to DTO and DTO to Entity
// It helps to keep Controller and Service clean by separating mapping logic

public class TodoMapper {

    // converts Todo Entity to TodoDTO (used for API response)
    public static TodoDTO mapToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setStatus(todo.getStatus());
        return dto;
    }

    // converts TodoDTO to Todo Entity (used before saving to database)
    public static Todo mapToEntity(TodoDTO dto) {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setStatus(dto.getStatus());
        return todo;
    }

}