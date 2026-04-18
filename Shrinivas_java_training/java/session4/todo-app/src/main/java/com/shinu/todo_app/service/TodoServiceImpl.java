package com.shinu.todo_app.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.shinu.todo_app.dto.TodoDTO;
import com.shinu.todo_app.entity.Todo;
import com.shinu.todo_app.entity.TodoStatus;
import com.shinu.todo_app.mapper.TodoMapper;
import com.shinu.todo_app.repository.TodoRepository;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public TodoDTO createTodo(TodoDTO dto) {
        Todo todo = TodoMapper.mapToEntity(dto);

        todo.setStatus(
                todo.getStatus() == null ? TodoStatus.PENDING : todo.getStatus());

        todo.setCreatedAt(LocalDateTime.now());

        Todo saved = todoRepository.save(todo);

        return TodoMapper.mapToDTO(saved);
    }

}
